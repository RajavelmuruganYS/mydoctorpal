// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, it } from "vitest";
import axios from "axios";
import { cleanup, fireEvent, render, screen, waitFor } from "@testing-library/react";
import { createElement } from "react";
import api from "../api/axios";
import App from "../App";
import { USERS, createAdapter } from "./fakeBackend";

let calls;
const reply = (status, data, config) => {
  if (status >= 400) {
    const error = new Error(`HTTP ${status}`);
    error.config = config;
    error.response = { status, data, config };
    throw error;
  }
  return { data, status, statusText: "OK", headers: {}, config };
};
const use = (handler) => {
  api.defaults.adapter = axios.defaults.adapter = async (config) => {
    const url = config.url.replace(config.baseURL || "", "");
    const body = config.data ? JSON.parse(config.data) : undefined;
    calls.push({ method: config.method, url, body, auth: config.headers?.Authorization });
    return handler({ method: config.method, url, body, config });
  };
};
const type = async (label, value) => fireEvent.change(await screen.findByLabelText(new RegExp(label, "i")), { target: { value } });
const open = (path) => {
  window.history.pushState({}, "", path);
  return render(createElement(App));
};

beforeEach(() => {
  calls = [];
  localStorage.clear();
});
afterEach(() => {
  cleanup();
  localStorage.clear();
});

describe("authentication flows", () => {
  it("shows the server message for a wrong password and stays on /login", async () => {
    use(({ url, config }) => (url === "/auth/login" ? reply(401, { success: false, message: "Invalid email or password" }, config) : reply(404, {}, config)));
    open("/login");
    await type("Email address", "a@b.com");
    await type("^Password", "wrong-password");
    fireEvent.submit(screen.getByRole("button", { name: /sign in/i }).closest("form"));
    expect(await screen.findByText(/Invalid email or password/)).toBeTruthy();
    expect(window.location.pathname).toBe("/login");
    expect(calls.filter((c) => c.url.endsWith("/auth/refresh"))).toEqual([]);
  });

  it("logs in, stores the session and lands on the role dashboard", async () => {
    const inner = createAdapter("DOCTOR", []);
    use(({ url, method, config }) =>
      url === "/auth/login"
        ? reply(200, { success: true, message: "Login successful", ...USERS.DOCTOR, accessToken: "AT", refreshToken: "RT" }, config)
        : inner({ ...config, url, method }),
    );
    open("/login");
    await type("Email address", "doctor@hospital.local");
    await type("^Password", "Doctor@12345");
    fireEvent.submit(screen.getByRole("button", { name: /sign in/i }).closest("form"));
    await waitFor(() => expect(window.location.pathname).toBe("/app/dashboard"));
    expect(JSON.parse(localStorage.getItem("mydoctorpal.session")).accessToken).toBe("AT");
    expect(calls.find((c) => c.url === "/auth/login").body).toEqual({ email: "doctor@hospital.local", password: "Doctor@12345" });
    await waitFor(() => expect(calls.some((c) => c.url === "/users/me" && c.auth === "Bearer AT")).toBe(true));
  });

  it("uses assigned patients for the doctor registry instead of the global patient list", async () => {
    localStorage.setItem("mydoctorpal.session", JSON.stringify({ accessToken: "AT", refreshToken: "RT", ...USERS.DOCTOR }));
    const inner = createAdapter("DOCTOR", []);
    use(({ url, method, config }) => inner({ ...config, url, method }));
    open("/app/patients");
    expect(await screen.findByText(/Patient registry/)).toBeTruthy();
    await waitFor(() => expect(calls.some((c) => c.url === "/doctors/me/patients")).toBe(true));
    expect(calls.some((c) => c.url === "/patients")).toBe(false);
  });

  it("verifies the email with email + otp (regression: broken subtraction call)", async () => {
    use(({ url, config }) => (url === "/auth/verify-email" ? reply(200, { success: true, message: "Email verified" }, config) : reply(404, {}, config)));
    open("/verify-email?email=new%40user.com");
    await type("One-time code", "123456");
    fireEvent.submit(screen.getByRole("button", { name: /verify email/i }).closest("form"));
    await waitFor(() => expect(window.location.pathname).toBe("/login"));
    expect(calls[0]).toMatchObject({ method: "post", url: "/auth/verify-email", body: { email: "new@user.com", otp: "123456" } });
  });

  it("runs the full password-reset chain and carries email + otp forward", async () => {
    use(({ config }) => reply(200, { success: true, message: "ok" }, config));
    open("/forgot-password");
    await type("Email address", "u@x.com");
    fireEvent.submit(screen.getByRole("button", { name: /send reset code/i }).closest("form"));
    await waitFor(() => expect(window.location.pathname).toBe("/verify-reset-otp"));
    await type("Reset code", "654321");
    fireEvent.submit(screen.getByRole("button", { name: /verify code/i }).closest("form"));
    await waitFor(() => expect(window.location.pathname).toBe("/reset-password"));
    await type("^New password", "NewPassw0rd!");
    await type("Confirm new password", "NewPassw0rd!");
    fireEvent.submit(screen.getByRole("button", { name: /reset password/i }).closest("form"));
    await waitFor(() => expect(window.location.pathname).toBe("/login"));
    expect(calls.map((c) => c.url)).toEqual(["/auth/forgot-password", "/auth/verify-reset-otp", "/auth/reset-password"]);
    expect(calls[2].body).toEqual({ email: "u@x.com", otp: "654321", newPassword: "NewPassw0rd!" });
  });

  it("refreshes an expired access token once and retries the request", async () => {
    localStorage.setItem("mydoctorpal.session", JSON.stringify({ accessToken: "OLD", refreshToken: "RT", ...USERS.ADMIN }));
    const inner = createAdapter("ADMIN", []);
    use(({ url, method, config }) => {
      if (url.endsWith("/auth/refresh")) return reply(200, { success: true, accessToken: "NEW", refreshToken: "RT2" }, config);
      if (url === "/patients" && config.headers?.Authorization === "Bearer OLD") return reply(401, { success: false, message: "Unauthorized" }, config);
      return inner({ ...config, url, method });
    });
    open("/app/patients");
    expect(await screen.findByText(/Patient registry/)).toBeTruthy();
    await waitFor(() => expect(screen.getByText("PAT-2026-000001")).toBeTruthy()).catch((e) => { console.log(JSON.stringify(calls.map((c) => c.method + " " + c.url + " " + c.auth))); throw e; });
    expect(calls.filter((c) => c.url.endsWith("/auth/refresh"))).toHaveLength(1);
    expect(JSON.parse(localStorage.getItem("mydoctorpal.session")).accessToken).toBe("NEW");
    expect(window.location.pathname).toBe("/app/patients");
  });

  it("sends the user to /login when refresh fails", async () => {
    localStorage.setItem("mydoctorpal.session", JSON.stringify({ accessToken: "OLD", refreshToken: "BAD", ...USERS.ADMIN }));
    const assigned = [];
    const realLocation = window.location;
    Object.defineProperty(window, "location", { configurable: true, value: { ...realLocation, pathname: "/app/patients", assign: (u) => assigned.push(u) } });
    use(({ url, config }) => reply(401, { success: false, message: url.endsWith("/auth/refresh") ? "Invalid refresh token" : "Unauthorized" }, config));
    open("/app/patients");
    await waitFor(() => expect(assigned[0]).toMatch(/^\/login/));
    expect(localStorage.getItem("mydoctorpal.session")).toBeNull();
    Object.defineProperty(window, "location", { configurable: true, value: realLocation });
  });
});

describe("write flows send what the backend expects", () => {
  const session = (role) => localStorage.setItem("mydoctorpal.session", JSON.stringify({ accessToken: "AT", refreshToken: "RT", ...USERS[role] }));

  it("patient books an appointment with their own patientId and a real doctor/department", async () => {
    session("PATIENT");
    const inner = createAdapter("PATIENT", []);
    use(({ url, method, body, config }) => (method === "post" && url === "/appointments" ? reply(200, { success: true, message: "Appointment booked", appointment: { id: 99 } }, config) : inner({ ...config, url, method, body })));
    open("/app/appointments/new");
    await screen.findByText("Book an appointment");
    await waitFor(() => expect(screen.getByRole("option", { name: "Dr. Daniel Carter" })).toBeTruthy());
    fireEvent.change(screen.getByLabelText(/^Doctor/), { target: { value: "2" } });
    fireEvent.change(screen.getByLabelText(/^Department/), { target: { value: "1" } });
    await type("^Date", "2030-02-01");
    await type("Start time", "10:00");
    await type("End time", "10:30");
    fireEvent.submit(screen.getByRole("button", { name: /book appointment/i }).closest("form"));
    await waitFor(() => expect(window.location.pathname).toBe("/app/appointments/99"));
    expect(calls.find((c) => c.url === "/appointments" && c.method === "post").body).toEqual({
      patientId: 1, doctorId: 2, departmentId: 1, appointmentDate: "2030-02-01", startTime: "10:00", endTime: "10:30",
    });
  });

  it("doctor creates a prescription with items built from the drug catalogue", async () => {
    session("DOCTOR");
    const inner = createAdapter("DOCTOR", []);
    use(({ url, method, body, config }) => (method === "post" && url === "/prescriptions" ? reply(200, { success: true, message: "created", prescription: { id: 55 } }, config) : inner({ ...config, url, method, body })));
    open("/app/prescriptions/new?patient=1");
    await screen.findByText("New prescription");
    await type("Diagnosis code", "J45.9");
    await waitFor(() => expect(screen.getByRole("option", { name: /Crocin/ })).toBeTruthy());
    fireEvent.change(screen.getByLabelText(/^Drug/), { target: { value: "1" } });
    await type("^Frequency", "Twice daily");
    await type("Duration", "5");
    fireEvent.submit(screen.getByRole("button", { name: /save draft/i }).closest("form"));
    await waitFor(() => expect(window.location.pathname).toBe("/app/prescriptions/55"));
    expect(calls.find((c) => c.url === "/prescriptions" && c.method === "post").body).toEqual({
      patientId: 1, diagnosisCode: "J45.9",
      items: [{ drugId: 1, dosageForm: "Tablet", strength: "500 mg", frequency: "Twice daily", durationDays: 5 }],
    });
  });

  it("drug search uses the backend's `name` parameter", async () => {
    session("PHARMACIST");
    const inner = createAdapter("PHARMACIST", []);
    use(({ url, method, config }) => inner({ ...config, url, method }));
    let seen;
    const spy = api.defaults.adapter;
    api.defaults.adapter = async (config) => { seen = seen || (config.url.includes("/drugs/search") ? config : undefined); return spy(config); };
    open("/app/drugs");
    await screen.findByText("Drug catalogue");
    fireEvent.change(screen.getByLabelText(/Search by brand name/i), { target: { value: "cro" } });
    await waitFor(() => expect(seen).toBeTruthy(), { timeout: 3000 });
    expect(seen.url).toBe("/drugs/search/brand");
    expect(seen.params).toEqual({ name: "cro" });
  });
});

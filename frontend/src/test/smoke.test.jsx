// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, it } from "vitest";
import { cleanup, render, waitFor } from "@testing-library/react";
import { createElement } from "react";
import api, { normalize } from "../api/axios";
import App from "../App";
import { USERS, createAdapter } from "./fakeBackend";

const PAGES = {
  PATIENT: ["/app/dashboard", "/app/patients/me", "/app/appointments", "/app/appointments/new", "/app/appointments/11", "/app/prescriptions", "/app/prescriptions/21", "/app/laboratory", "/app/laboratory/orders", "/app/laboratory/orders/41", "/app/laboratory/results", "/app/inpatient", "/app/inpatient/61", "/app/vitals", "/app/radiology", "/app/radiology/orders", "/app/radiology/orders/81", "/app/notifications", "/app/profile"],
  DOCTOR: ["/app/dashboard", "/app/patients", "/app/patients/1", "/app/patients/1/edit", "/app/appointments", "/app/appointments/11", "/app/prescriptions", "/app/prescriptions/new", "/app/prescriptions/21", "/app/drugs", "/app/laboratory", "/app/laboratory/orders", "/app/laboratory/orders/new", "/app/laboratory/orders/41", "/app/laboratory/results", "/app/inpatient", "/app/inpatient/new", "/app/inpatient/61", "/app/vitals?patient=1", "/app/vitals/new", "/app/radiology", "/app/radiology/orders", "/app/radiology/orders/new", "/app/radiology/orders/81", "/app/radiology/studies", "/app/radiology/reports", "/app/notifications"],
  NURSE: ["/app/dashboard", "/app/patients", "/app/patients/1", "/app/appointments", "/app/prescriptions?patient=1", "/app/laboratory", "/app/laboratory/orders", "/app/inpatient", "/app/vitals?patient=1", "/app/radiology", "/app/radiology/orders", "/app/radiology/orders/81"],
  LAB_TECHNICIAN: ["/app/dashboard", "/app/laboratory", "/app/laboratory/orders", "/app/laboratory/orders/41", "/app/laboratory/results", "/app/patients/1", "/app/radiology", "/app/radiology/orders", "/app/radiology/orders/81", "/app/radiology/studies", "/app/radiology/reports"],
  PHARMACIST: ["/app/dashboard", "/app/pharmacy", "/app/pharmacy/dispense/21", "/app/pharmacy/dispensations/31", "/app/prescriptions", "/app/prescriptions/21", "/app/drugs"],
  ADMIN: ["/app/dashboard", "/app/admin", "/app/admin/users", "/app/admin/audit-logs", "/app/patients", "/app/patients/new", "/app/appointments", "/app/appointments/new", "/app/drugs", "/app/pharmacy", "/app/laboratory", "/app/laboratory/results", "/app/inpatient", "/app/radiology", "/app/notifications", "/app/profile"],
};

describe("response normalisation", () => {
  it("unwraps single-payload envelopes and leaves raw bodies alone", () => {
    expect(normalize({ success: true, count: 2, appointments: [1, 2] })).toEqual([1, 2]);
    expect(normalize({ success: true, message: "m", patient: { id: 1 } })).toEqual({ id: 1 });
    expect(normalize([1])).toEqual([1]);
    expect(normalize(5)).toBe(5);
    expect(normalize({ success: true, userId: 1, email: "a" })).toEqual({ userId: 1, email: "a" });
    expect(normalize({ id: 3 })).toEqual({ id: 3 });
  });
});

describe.each(Object.keys(PAGES))("%s pages render against backend-shaped responses", (role) => {
  let errors;
  const original = console.error;
  beforeEach(() => {
    errors = [];
    console.error = (...args) => errors.push(args.map(String).join(" "));
    localStorage.setItem("mydoctorpal.session", JSON.stringify({ accessToken: "t", refreshToken: "r", ...USERS[role] }));
    api.defaults.adapter = createAdapter(role, (globalThis.__calls = []));
  });
  afterEach(() => {
    cleanup();
    console.error = original;
    localStorage.clear();
  });

  it.each(PAGES[role])("%s", async (path) => {
    window.history.pushState({}, "", path);
    const { container } = render(createElement(App));
    await waitFor(() => expect(container.querySelector("h1")).not.toBeNull(), { timeout: 4000 });
    await new Promise((r) => setTimeout(r, 60)); // let secondary requests settle
    const text = container.textContent;
    expect(text).not.toMatch(/Something needs attention/);
    expect(text).not.toMatch(/Unmocked|could not be loaded/);
    expect(globalThis.__calls.filter((c) => c.startsWith("MISS"))).toEqual([]);
    expect(globalThis.__calls.length).toBeGreaterThan(0);
    (globalThis.__seen ||= new Set()).add(...globalThis.__calls);
    expect(window.location.pathname).toBe(path.split("?")[0]); // not bounced to /403 or /login
    const reactErrors = errors.filter((e) => /Warning|Error|Uncaught|not wrapped/i.test(e) && !/act\(/.test(e));
    expect(reactErrors).toEqual([]);
  });
});

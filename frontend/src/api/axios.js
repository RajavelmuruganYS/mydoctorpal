import axios from "axios";
import { API_BASE_URL } from "../utils/constants";
import {
  getAccessToken,
  getRefreshToken,
  saveTokens,
} from "../services/tokenService";
import { clearSession } from "../services/storageService";

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: { "Content-Type": "application/json" },
});

/**
 * The backend answers in two styles:
 *   1. wrapped:  { success, message, appointments: [...] }   (appointments, labs, pharmacy, drugs, prescriptions, patients)
 *   2. raw:      [ ... ]  or  { ...entity }                    (audit, inpatient, radiology, vitals, notifications, admin)
 * normalize() unwraps style 1 so every API function resolves to the payload itself:
 *   { success, count, patients: [...] } -> [...]
 *   { success, message, appointment }   -> { ...appointment }
 * The server's message (if any) is exposed as response.message.
 */
export function normalize(data) {
  if (!data || typeof data !== "object" || Array.isArray(data)) return data;
  if (typeof data.success !== "boolean") return data;
  // eslint-disable-next-line no-unused-vars
  const { success, message, count, ...rest } = data;
  const keys = Object.keys(rest);
  if (keys.length === 1) return rest[keys[0]];
  return rest;
}

const isAuthUrl = (url = "") => url.includes("/auth/");

api.interceptors.request.use((config) => {
  const token = getAccessToken();
  if (token && !isAuthUrl(config.url)) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

let refreshing = null;

const forceLogout = () => {
  clearSession();
  if (!window.location.pathname.startsWith("/login")) {
    window.location.assign("/login?expired=1");
  }
};

api.interceptors.response.use(
  (response) => {
    response.message = response.data?.message;
    response.data = normalize(response.data);
    return response;
  },
  async (error) => {
    const original = error.config;
    const status = error.response?.status;

    // Only an expired/invalid access token on a protected endpoint is retried.
    // Auth endpoints (login, refresh, ...) never trigger a refresh, so a wrong
    // password shows its own message instead of bouncing the page.
    if (status !== 401 || !original || original._retry || isAuthUrl(original.url)) {
      return Promise.reject(error);
    }

    const refreshToken = getRefreshToken();
    if (!refreshToken) {
      forceLogout();
      return Promise.reject(error);
    }

    original._retry = true;

    try {
      if (!refreshing) {
        refreshing = axios
          .post(`${API_BASE_URL}/auth/refresh`, { refreshToken })
          .finally(() => {
            refreshing = null;
          });
      }
      const { data } = await refreshing;
      saveTokens(data.accessToken, data.refreshToken);
      original.headers.Authorization = `Bearer ${data.accessToken}`;
      return api(original);
    } catch (refreshError) {
      forceLogout();
      return Promise.reject(refreshError);
    }
  },
);

export default api;

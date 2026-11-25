import api from "./axios";
export const authApi = {
  register: (body) => api.post("/auth/register", body),
  verifyEmail: (body) => api.post("/auth/verify-email", body),
  login: (body) => api.post("/auth/login", body),
  refresh: (body) => api.post("/auth/refresh", body),
  logout: (body) => api.post("/auth/logout", body),
  forgotPassword: (body) => api.post("/auth/forgot-password", body),
  verifyResetOtp: (body) => api.post("/auth/verify-reset-otp", body),
  resetPassword: (body) => api.post("/auth/reset-password", body),
  resendOtp: (body) => api.post("/auth/resend-otp", body),
};

import api from "./axios";
export const dashboardApi = {
  patient: () => api.get("/dashboard/patient"),
  doctor: () => api.get("/dashboard/doctor"),
  nurse: () => api.get("/dashboard/nurse"),
  lab: () => api.get("/dashboard/lab"),
  pharmacy: () => api.get("/dashboard/pharmacy"),
  admin: () => api.get("/dashboard/admin"),
};

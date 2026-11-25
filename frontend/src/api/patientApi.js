import api from "./axios";
export const patientApi = {
  me: () => api.get("/patients/me"),
  list: () => api.get("/patients"),
  get: (id) => api.get(`/patients/${id}`),
  byNumber: (number) => api.get(`/patients/number/${number}`),
  create: (body) => api.post("/patients", body),
  update: (id, body) => api.put(`/patients/${id}`, body),
  remove: (id) => api.delete(`/patients/${id}`),
};

import api from "./axios";
export const vitalsApi = {
  create: (body) => api.post("/vitals", body),
  get: (id) => api.get(`/vitals/${id}`),
  byPatient: (id) => api.get(`/vitals/patients/${id}`),
  latest: (id) => api.get(`/vitals/patients/${id}/latest`),
  byUser: (id) => api.get(`/vitals/users/${id}`),
};

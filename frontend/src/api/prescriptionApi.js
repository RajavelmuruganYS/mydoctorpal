import api from "./axios";
export const prescriptionApi = {
  create: (body) => api.post("/prescriptions", body),
  sign: (id) => api.post(`/prescriptions/${id}/sign`),
  get: (id) => api.get(`/prescriptions/${id}`),
  byPatient: (id) => api.get(`/prescriptions/patient/${id}`),
  byDoctor: (id) => api.get(`/prescriptions/doctor/${id}`),
  items: (id) => api.get(`/prescriptions/${id}/items`),
  drugs: () => api.get("/drugs"),
  drug: (id) => api.get(`/drugs/${id}`),
  searchBrand: (q) =>
    api.get("/drugs/search/brand", { params: { name: q } }),
  searchGeneric: (q) =>
    api.get("/drugs/search/generic", { params: { name: q } }),
  createDrug: (body) => api.post("/drugs", body),
  deactivateDrug: (id) => api.put(`/drugs/${id}/deactivate`),
  activateDrug: (id) => api.put(`/drugs/${id}/activate`),
};

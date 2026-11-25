import api from "./axios";
export const inpatientApi = {
  create: (body) => api.post("/inpatient/admissions", body),
  discharge: (id, body) =>
    api.put(`/inpatient/admissions/${id}/discharge`, body),
  get: (id) => api.get(`/inpatient/admissions/${id}`),
  byPatient: (id) => api.get(`/inpatient/patients/${id}/admissions`),
  byDoctor: (id) => api.get(`/inpatient/doctors/${id}/admissions`),
  byStatus: (status) => api.get(`/inpatient/admissions/status/${status}`),
  byCategory: (category) =>
    api.get(`/inpatient/admissions/category/${category}`),
};

import api from "./axios";
export const appointmentApi = {
  create: (body) => api.post("/appointments", body),
  list: () => api.get("/appointments"),
  mine: () => api.get("/appointments/me"),
  get: (id) => api.get(`/appointments/${id}`),
  byPatient: (id) => api.get(`/appointments/patient/${id}`),
  byDoctor: (id) => api.get(`/appointments/doctor/${id}`),
  byDate: (date) => api.get(`/appointments/date/${date}`),
  byPatientDate: (id, date) =>
    api.get(`/appointments/patient/${id}/date/${date}`),
  byDoctorDate: (id, date) =>
    api.get(`/appointments/doctor/${id}/date/${date}`),
  status: (id, status) => api.put(`/appointments/${id}/status`, { status }),
  cancel: (id, cancellationReason) =>
    api.put(`/appointments/${id}/cancel`, { cancellationReason }),
  remove: (id) => api.delete(`/appointments/${id}`),
};

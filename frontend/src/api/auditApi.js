import api from "./axios";
export const auditApi = {
  get: (id) => api.get(`/audit-logs/${id}`),
  byUser: (id) => api.get(`/audit-logs/users/${id}`),
  byPatient: (id) => api.get(`/audit-logs/patients/${id}`),
  byAction: (action) => api.get(`/audit-logs/actions/${action}`),
  byResource: (type, id) => api.get(`/audit-logs/resources/${type}/${id}`),
  byDateRange: (params) => api.get("/audit-logs/date-range", { params }),
};

import api from "./axios";
export const laboratoryApi = {
  createOrder: (body) => api.post("/laboratory/orders", body),
  addResult: (id, body) => api.post(`/laboratory/orders/${id}/results`, body),
  order: (id) => api.get(`/laboratory/orders/${id}`),
  result: (id) => api.get(`/laboratory/results/${id}`),
  patientOrders: (id) => api.get(`/laboratory/patients/${id}/orders`),
  doctorOrders: (id) => api.get(`/laboratory/doctors/${id}/orders`),
  byStatus: (status) => api.get(`/laboratory/orders/status/${status}`),
  orderResults: (id) => api.get(`/laboratory/orders/${id}/results`),
  technicianResults: (id) => api.get(`/laboratory/technicians/${id}/results`),
};

import api from "./axios";
export const radiologyApi = {
  createOrder: (body) => api.post("/radiology/orders", body),
  updateOrderStatus: (id, status) =>
    api.put(`/radiology/orders/${id}/status`, { status }),
  order: (id) => api.get(`/radiology/orders/${id}`),
  patientOrders: (id) => api.get(`/radiology/orders/patients/${id}`),
  patientOrdersByStatus: (id, status) =>
    api.get(`/radiology/orders/patients/${id}/status/${status}`),
  doctorOrders: (id) => api.get(`/radiology/orders/doctors/${id}`),
  doctorOrdersByStatus: (id, status) =>
    api.get(`/radiology/orders/doctors/${id}/status/${status}`),
  ordersByStatus: (status) => api.get(`/radiology/orders/status/${status}`),
  ordersByModality: (modality) =>
    api.get(`/radiology/orders/modality/${modality}`),
  ordersByUrgency: (urgency) => api.get(`/radiology/orders/urgency/${urgency}`),
  createReport: (body) => api.post("/radiology/reports", body),
  finalizeReport: (id) => api.put(`/radiology/reports/${id}/finalize`),
  amendReport: (id, body) => api.put(`/radiology/reports/${id}/amend`, body),
  report: (id) => api.get(`/radiology/reports/${id}`),
  reportByOrder: (id) => api.get(`/radiology/reports/orders/${id}`),
  reportsByUser: (id) => api.get(`/radiology/reports/users/${id}`),
  reportsByStatus: (s) => api.get(`/radiology/reports/status/${s}`),
  criticalReports: () => api.get("/radiology/reports/critical"),
  createStudy: (body) => api.post("/radiology/studies", body),
  study: (id) => api.get(`/radiology/studies/${id}`),
  studiesByOrder: (id) => api.get(`/radiology/studies/orders/${id}`),
  latestStudy: (id) => api.get(`/radiology/studies/orders/${id}/latest`),
  byAccession: (v) => api.get(`/radiology/studies/accession/${v}`),
  byUid: (v) => api.get(`/radiology/studies/uid/${v}`),
  studiesByStatus: (s) => api.get(`/radiology/studies/status/${s}`),
  uploadStudyFile: (studyId, file) => {
    const formData = new FormData();
    formData.append("file", file);
    return api.post(`/radiology/studies/${studyId}/files`, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
  },
  studyFiles: (studyId) => api.get(`/radiology/studies/${studyId}/files`),
  studyFile: (studyId, fileId) => api.get(`/radiology/studies/${studyId}/files/${fileId}`),
  downloadStudyFile: (studyId, fileId) => api.get(`/radiology/studies/${studyId}/files/${fileId}/download`, { responseType: "blob" }),
};

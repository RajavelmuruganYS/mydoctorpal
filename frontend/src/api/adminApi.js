import api from "./axios";
export const adminApi = {
  users: () => api.get("/admin/users"),
  user: (id) => api.get(`/admin/users/${id}`),
  byRole: (role) => api.get(`/admin/users/role/${role}`),
  staff: () => api.get("/admin/staff"),
  createStaff: (body) => api.post("/admin/staff", body),
  updateStaffStatus: (id, status) => api.patch(`/admin/staff/${id}/status`, { status }),
  assignedDoctors: (patientId) => api.get(`/admin/patients/${patientId}/doctors`),
  assignDoctor: (patientId, doctorId) => api.post(`/admin/patients/${patientId}/doctors/${doctorId}`),
  revokeDoctor: (patientId, doctorId) => api.delete(`/admin/patients/${patientId}/doctors/${doctorId}`),
};

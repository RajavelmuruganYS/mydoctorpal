import api from "./axios";

export const directoryApi = {
  doctors: () => api.get("/directory/doctors"),
  departments: () => api.get("/directory/departments"),
  assignedPatients: () => api.get("/doctors/me/patients"),
};

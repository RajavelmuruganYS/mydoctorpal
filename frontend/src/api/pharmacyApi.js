import api from "./axios";
export const pharmacyApi = {
  dispense: (prescriptionId, body) =>
    api.post(`/pharmacy/prescriptions/${prescriptionId}/dispense`, body),
  getDispensation: (id) => api.get(`/pharmacy/dispensations/${id}`),
  byPrescription: (id) =>
    api.get(`/pharmacy/prescriptions/${id}/dispensations`),
  byPharmacist: (id) => api.get(`/pharmacy/pharmacists/${id}/dispensations`),
  byStatus: (status) => api.get(`/pharmacy/dispensations/status/${status}`),
};

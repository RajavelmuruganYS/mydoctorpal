import { laboratoryApi } from "../../api/laboratoryApi";

/**
 * Which lab-order endpoint fits the current user?
 *  - patient          -> their own orders
 *  - chosen patient   -> that patient's orders
 *  - doctor           -> orders they placed
 *  - lab / nurse / admin -> the work queue by status
 */
export const loadLabOrders = ({ isPatient, ownId, scopePatientId, role, userId, status }) => {
  const patientId = isPatient ? ownId : scopePatientId;
  if (patientId) return laboratoryApi.patientOrders(patientId);
  if (role === "DOCTOR") return laboratoryApi.doctorOrders(userId);
  return laboratoryApi.byStatus(status);
};

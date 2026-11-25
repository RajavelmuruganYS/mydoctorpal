import { useMemo } from "react";
import { useApi } from "./useApi";
import { useAuth } from "../context/AuthContext";
import { directoryApi } from "../api/directoryApi";
import { patientApi } from "../api/patientApi";
import { asList, personName } from "../utils/payload";
import { CAN_LIST_PATIENTS } from "../utils/permissions";

/**
 * Name lookups so screens can show "Dr. Carter" instead of "doctor #2".
 * Doctors and departments are available to everyone; the patient list only
 * to doctors, nurses and admins (the backend does not allow it for others).
 */
export const useDirectory = () => {
  const { hasRole, role } = useAuth();
  const canListPatients = hasRole(CAN_LIST_PATIENTS);
  const doctors = useApi(() => directoryApi.doctors(), []);
  const departments = useApi(() => directoryApi.departments(), []);
  const patients = useApi(
    () => role === "DOCTOR" ? directoryApi.assignedPatients() : patientApi.list(),
    [role],
    canListPatients,
  );

  return useMemo(() => {
    const doctorList = asList(doctors.data);
    const departmentList = asList(departments.data);
    const patientList = asList(patients.data);
    return {
      doctors: doctorList,
      departments: departmentList,
      patients: patientList,
      loading: doctors.loading || departments.loading || patients.loading,
      doctorName: (id) => {
        const doctor = doctorList.find((d) => d.userId === id);
        return doctor ? `Dr. ${personName(doctor)}` : id ? `Doctor #${id}` : "—";
      },
      patientName: (id) => {
        const patient = patientList.find((p) => p.id === id);
        return patient ? personName(patient) || patient.patientNumber : id ? `Patient #${id}` : "—";
      },
      departmentName: (id) => departmentList.find((d) => d.id === id)?.name || (id ? `Department #${id}` : "—"),
    };
  }, [doctors.data, doctors.loading, departments.data, departments.loading, patients.data, patients.loading]);
};

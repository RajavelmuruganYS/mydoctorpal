import { useAuth } from "../../context/AuthContext";
import PatientDashboard from "./PatientDashboard";
import DoctorDashboard from "./DoctorDashboard";
import NurseDashboard from "./NurseDashboard";
import LabDashboard from "./LabDashboard";
import PharmacyDashboard from "./PharmacyDashboard";
import AdminDashboard from "./AdminDashboard";

const BY_ROLE = {
  PATIENT: PatientDashboard,
  DOCTOR: DoctorDashboard,
  NURSE: NurseDashboard,
  LAB_TECHNICIAN: LabDashboard,
  PHARMACIST: PharmacyDashboard,
  ADMIN: AdminDashboard,
};

export default function RoleDashboard() {
  const { role } = useAuth();
  const Dashboard = BY_ROLE[role] || PatientDashboard;
  return <Dashboard />;
}

import { lazy } from "react";
import { Navigate, Route, Routes } from "react-router-dom";
import PublicLayout from "../pages/PublicLayout";
import Landing from "../pages/Landing";
import Login from "../pages/auth/Login";
import Register from "../pages/auth/Register";
import VerifyOtp from "../pages/auth/VerifyOtp";
import ForgotPassword from "../pages/auth/ForgotPassword";
import VerifyResetOtp from "../pages/auth/VerifyResetOtp";
import ResetPassword from "../pages/auth/ResetPassword";
import MainLayout from "../components/layout/MainLayout";
import ProtectedRoute from "./ProtectedRoute";
import RoleRoute from "./RoleRoute";
import NotFound from "../pages/errors/NotFound";
import Unauthorized from "../pages/errors/Unauthorized";
import ServerError from "../pages/errors/ServerError";

const page = (loader) => lazy(loader);
const RoleDashboard = page(() => import("../pages/dashboard/RoleDashboard"));
const MyProfile = page(() => import("../pages/profile/MyProfile"));
const Notifications = page(() => import("../pages/notifications/Notifications"));
const Patients = page(() => import("../pages/patient/Patients"));
const PatientDetails = page(() => import("../pages/patient/PatientDetails"));
const PatientProfile = page(() => import("../pages/patient/PatientProfile"));
const CreatePatient = page(() => import("../pages/patient/CreatePatient"));
const EditPatient = page(() => import("../pages/patient/EditPatient"));
const Appointments = page(() => import("../pages/appointments/Appointments"));
const CreateAppointment = page(() => import("../pages/appointments/CreateAppointment"));
const AppointmentDetails = page(() => import("../pages/appointments/AppointmentDetails"));
const Prescriptions = page(() => import("../pages/prescriptions/Prescriptions"));
const CreatePrescription = page(() => import("../pages/prescriptions/CreatePrescription"));
const PrescriptionDetails = page(() => import("../pages/prescriptions/PrescriptionDetails"));
const Drugs = page(() => import("../pages/prescriptions/Drugs"));
const Pharmacy = page(() => import("../pages/pharmacy/Pharmacy"));
const DispensePrescription = page(() => import("../pages/pharmacy/DispensePrescription"));
const DispensationDetails = page(() => import("../pages/pharmacy/DispensationDetails"));
const Laboratory = page(() => import("../pages/laboratory/Laboratory"));
const LabOrders = page(() => import("../pages/laboratory/LabOrders"));
const CreateLabOrder = page(() => import("../pages/laboratory/CreateLabOrder"));
const LabOrderDetails = page(() => import("../pages/laboratory/LabOrderDetails"));
const LabResults = page(() => import("../pages/laboratory/LabResults"));
const Admissions = page(() => import("../pages/inpatient/Admissions"));
const CreateAdmission = page(() => import("../pages/inpatient/CreateAdmission"));
const AdmissionDetails = page(() => import("../pages/inpatient/AdmissionDetails"));
const Vitals = page(() => import("../pages/vitals/Vitals"));
const AddVitals = page(() => import("../pages/vitals/AddVitals"));
const Radiology = page(() => import("../pages/radiology/Radiology"));
const RadiologyWorklist = page(() => import("../pages/radiology/RadiologyWorklist"));
const RadiologyOrders = page(() => import("../pages/radiology/RadiologyOrders"));
const CreateRadiologyOrder = page(() => import("../pages/radiology/CreateRadiologyOrder"));
const RadiologyOrderDetails = page(() => import("../pages/radiology/RadiologyOrderDetails"));
const RadiologyViewer = page(() => import("../pages/radiology/RadiologyViewer"));
const RadiologyStudies = page(() => import("../pages/radiology/RadiologyStudies"));
const RadiologyReports = page(() => import("../pages/radiology/RadiologyReports"));
const AdminSystemOverview = page(() => import("../pages/admin/AdminSystemOverview"));
const AdminUsers = page(() => import("../pages/admin/AdminUsers"));
const StaffManagement = page(() => import("../pages/admin/StaffManagement"));
const AdminAuditLogs = page(() => import("../pages/admin/AdminAuditLogs"));

const STAFF = ["DOCTOR", "NURSE", "RADIOLOGY_TECHNICIAN", "ADMIN"];
const guard = (roles, element) => <RoleRoute roles={roles}>{element}</RoleRoute>;

// Route access mirrors the backend @PreAuthorize rules; the backend stays the authority.
export default function AppRoutes() {
  return (
    <Routes>
      <Route element={<PublicLayout />}>
        <Route path="/" element={<Landing />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/verify-email" element={<VerifyOtp />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/verify-reset-otp" element={<VerifyResetOtp />} />
        <Route path="/reset-password" element={<ResetPassword />} />
      </Route>
      <Route path="/403" element={<Unauthorized />} />
      <Route path="/500" element={<ServerError />} />

      <Route element={<ProtectedRoute><MainLayout /></ProtectedRoute>}>
        <Route path="/app" element={<Navigate to="/app/dashboard" replace />} />
        <Route path="/app/dashboard" element={<RoleDashboard />} />
        <Route path="/app/profile" element={<MyProfile />} />
        <Route path="/app/notifications" element={<Notifications />} />

        <Route path="/app/patients" element={guard(STAFF, <Patients />)} />
        <Route path="/app/patients/new" element={guard(["ADMIN"], <CreatePatient />)} />
        <Route path="/app/patients/me" element={guard(["PATIENT"], <PatientProfile />)} />
        <Route path="/app/patients/:id" element={guard([...STAFF, "LAB_TECHNICIAN", "PHARMACIST"], <PatientDetails />)} />
        <Route path="/app/patients/:id/edit" element={guard(STAFF, <EditPatient />)} />

        <Route path="/app/appointments" element={guard(["PATIENT", ...STAFF], <Appointments />)} />
        <Route path="/app/appointments/new" element={guard(["PATIENT", "ADMIN"], <CreateAppointment />)} />
        <Route path="/app/appointments/:id" element={guard(["PATIENT", ...STAFF], <AppointmentDetails />)} />

        <Route path="/app/prescriptions" element={guard(["PATIENT", "PHARMACIST", ...STAFF], <Prescriptions />)} />
        <Route path="/app/prescriptions/new" element={guard(["DOCTOR"], <CreatePrescription />)} />
        <Route path="/app/prescriptions/:id" element={guard(["PATIENT", "PHARMACIST", ...STAFF], <PrescriptionDetails />)} />
        <Route path="/app/drugs" element={guard(["DOCTOR", "PHARMACIST", "ADMIN"], <Drugs />)} />

        <Route path="/app/pharmacy" element={guard(["PHARMACIST", "ADMIN"], <Pharmacy />)} />
        <Route path="/app/pharmacy/dispense/:prescriptionId" element={guard(["PHARMACIST"], <DispensePrescription />)} />
        <Route path="/app/pharmacy/dispensations/:id" element={guard(["PHARMACIST", "ADMIN"], <DispensationDetails />)} />

        <Route path="/app/laboratory" element={guard(["PATIENT", "LAB_TECHNICIAN", ...STAFF], <Laboratory />)} />
        <Route path="/app/laboratory/orders" element={guard(["PATIENT", "LAB_TECHNICIAN", ...STAFF], <LabOrders />)} />
        <Route path="/app/laboratory/orders/new" element={guard(["DOCTOR"], <CreateLabOrder />)} />
        <Route path="/app/laboratory/orders/:id" element={guard(["PATIENT", "LAB_TECHNICIAN", ...STAFF], <LabOrderDetails />)} />
        <Route path="/app/laboratory/results" element={guard(["PATIENT", "LAB_TECHNICIAN", ...STAFF], <LabResults />)} />

        <Route path="/app/inpatient" element={guard(["PATIENT", ...STAFF], <Admissions />)} />
        <Route path="/app/inpatient/new" element={guard(["DOCTOR"], <CreateAdmission />)} />
        <Route path="/app/inpatient/:id" element={guard(["PATIENT", ...STAFF], <AdmissionDetails />)} />

        <Route path="/app/vitals" element={guard(["PATIENT", ...STAFF], <Vitals />)} />
        <Route path="/app/vitals/new" element={guard(["DOCTOR", "NURSE"], <AddVitals />)} />

        <Route path="/app/radiology" element={guard(["PATIENT", "LAB_TECHNICIAN", "RADIOLOGY_TECHNICIAN", ...STAFF], <Radiology />)} />
        <Route path="/app/radiology/worklist" element={guard(["LAB_TECHNICIAN", "RADIOLOGY_TECHNICIAN", ...STAFF], <RadiologyWorklist />)} />
        <Route path="/app/radiology/orders" element={guard(["PATIENT", "LAB_TECHNICIAN", "RADIOLOGY_TECHNICIAN", ...STAFF], <RadiologyOrders />)} />
        <Route path="/app/radiology/orders/new" element={guard(["DOCTOR"], <CreateRadiologyOrder />)} />
        <Route path="/app/radiology/orders/:id" element={guard(["PATIENT", "LAB_TECHNICIAN", "RADIOLOGY_TECHNICIAN", ...STAFF], <RadiologyOrderDetails />)} />
        <Route path="/app/radiology/viewer/:studyId" element={guard(["PATIENT", "LAB_TECHNICIAN", "RADIOLOGY_TECHNICIAN", ...STAFF], <RadiologyViewer />)} />
        <Route path="/app/radiology/studies" element={guard(["LAB_TECHNICIAN", "RADIOLOGY_TECHNICIAN", ...STAFF], <RadiologyStudies />)} />
        <Route path="/app/radiology/reports" element={guard(["LAB_TECHNICIAN", "RADIOLOGY_TECHNICIAN", ...STAFF], <RadiologyReports />)} />

        <Route path="/app/admin" element={guard(["ADMIN"], <AdminSystemOverview />)} />
        <Route path="/app/admin/users" element={guard(["ADMIN"], <AdminUsers />)} />
        <Route path="/app/admin/staff" element={guard(["ADMIN"], <StaffManagement />)} />
        <Route path="/app/admin/audit-logs" element={guard(["ADMIN"], <AdminAuditLogs />)} />
      </Route>
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}

// Navigation / UX visibility only. The backend enforces the real authorization.
const ALL = ["PATIENT", "DOCTOR", "NURSE", "LAB_TECHNICIAN", "RADIOLOGY_TECHNICIAN", "PHARMACIST", "ADMIN"];

export const NAV_ITEMS = [
  { label: "Dashboard", to: "/app/dashboard", icon: "LayoutDashboard", roles: ALL },
  { label: "My profile", to: "/app/patients/me", icon: "UserRound", roles: ["PATIENT"] },
  { label: "Patients", to: "/app/patients", icon: "Users", roles: ["DOCTOR", "NURSE", "ADMIN"] },
  { label: "Appointments", to: "/app/appointments", icon: "CalendarDays", roles: ["PATIENT", "DOCTOR", "NURSE", "ADMIN"] },
  { label: "Prescriptions", to: "/app/prescriptions", icon: "Pill", roles: ["PATIENT", "DOCTOR", "NURSE", "PHARMACIST", "ADMIN"] },
  { label: "Drugs", to: "/app/drugs", icon: "Tablets", roles: ["DOCTOR", "PHARMACIST", "ADMIN"] },
  { label: "Pharmacy", to: "/app/pharmacy", icon: "ShoppingBag", roles: ["PHARMACIST", "ADMIN"] },
  { label: "Laboratory", to: "/app/laboratory", icon: "TestTube2", roles: ["PATIENT", "DOCTOR", "NURSE", "LAB_TECHNICIAN", "ADMIN"] },
  { label: "Radiology", to: "/app/radiology", icon: "ScanLine", roles: ["PATIENT", "DOCTOR", "NURSE", "RADIOLOGY_TECHNICIAN", "LAB_TECHNICIAN", "ADMIN"] },
  { label: "Admissions", to: "/app/inpatient", icon: "BedDouble", roles: ["PATIENT", "DOCTOR", "NURSE", "ADMIN"] },
  { label: "Vitals", to: "/app/vitals", icon: "Activity", roles: ["PATIENT", "DOCTOR", "NURSE", "ADMIN"] },
  { label: "Notifications", to: "/app/notifications", icon: "Bell", roles: ALL },
  { label: "Admin overview", to: "/app/admin", icon: "ShieldCheck", roles: ["ADMIN"], end: true },
  { label: "Users", to: "/app/admin/users", icon: "UsersRound", roles: ["ADMIN"] },
  { label: "Staff management", to: "/app/admin/staff", icon: "UsersRound", roles: ["ADMIN"] },
  { label: "Audit logs", to: "/app/admin/audit-logs", icon: "ScrollText", roles: ["ADMIN"] },
];

export const STAFF = ["DOCTOR", "NURSE", "ADMIN"];
export const CAN_LIST_PATIENTS = ["DOCTOR", "NURSE", "ADMIN"];

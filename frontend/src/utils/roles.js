export const ROLES = {
  PATIENT: "PATIENT",
  DOCTOR: "DOCTOR",
  NURSE: "NURSE",
  LAB_TECHNICIAN: "LAB_TECHNICIAN",
  RADIOLOGY_TECHNICIAN: "RADIOLOGY_TECHNICIAN",
  PHARMACIST: "PHARMACIST",
  ADMIN: "ADMIN",
};

// A user with several roles is treated as their highest-privilege role in the UI.
// The backend remains the authority on what each role can actually do.
export const ROLE_PRIORITY = ["ADMIN", "DOCTOR", "NURSE", "RADIOLOGY_TECHNICIAN", "LAB_TECHNICIAN", "PHARMACIST", "PATIENT"];

export const primaryRole = (roles = []) =>
  ROLE_PRIORITY.find((role) => roles.includes(role)) || roles[0] || "PATIENT";

export const roleLabel = (role = "") =>
  role
    .replaceAll("_", " ")
    .toLowerCase()
    .replace(/\b\w/g, (c) => c.toUpperCase());

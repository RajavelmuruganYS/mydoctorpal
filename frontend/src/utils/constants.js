export const APP_NAME = "mydoctorpal";
export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1";

// Exact values used by the backend services.
export const APPOINTMENT_STATUSES = ["SCHEDULED", "CONFIRMED", "COMPLETED", "CANCELLED", "NO_SHOW"];
export const LAB_STATUSES = ["ORDERED", "RESULT_AVAILABLE"];
export const PRIORITIES = ["ROUTINE", "URGENT", "STAT"];
export const ADMISSION_STATUSES = ["ADMITTED", "DISCHARGED"];
export const ADMISSION_CATEGORIES = ["GENERAL", "EMERGENCY", "SURGERY", "ICU", "MATERNITY", "PEDIATRIC"];
export const MODALITIES = ["XRAY", "CT", "MRI", "ULTRASOUND"];
export const RADIOLOGY_ORDER_STATUSES = ["ORDERED", "SCHEDULED", "ARRIVED", "IN_PROGRESS", "COMPLETED", "CANCELLED"];
export const REPORT_STATUSES = ["DRAFT", "FINAL", "AMENDED"];
export const STUDY_STATUSES = ["PENDING", "AVAILABLE", "ARCHIVED", "FAILED"];
export const STORAGE_TYPES = ["PACS", "DICOM_SERVER", "LOCAL", "EXTERNAL"];
export const GENDERS = ["MALE", "FEMALE", "OTHER"];
export const BLOOD_GROUPS = ["A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"];

export const STATUS_COLORS = {
  ACTIVE: "bg-emerald-100 text-emerald-700",
  AVAILABLE: "bg-emerald-100 text-emerald-700",
  SIGNED: "bg-emerald-100 text-emerald-700",
  DISPENSED: "bg-emerald-100 text-emerald-700",
  CONFIRMED: "bg-emerald-100 text-emerald-700",
  FINAL: "bg-emerald-100 text-emerald-700",
  RESULT_AVAILABLE: "bg-emerald-100 text-emerald-700",
  ADMITTED: "bg-sky-100 text-sky-700",
  SCHEDULED: "bg-sky-100 text-sky-700",
  ARRIVED: "bg-sky-100 text-sky-700",
  IN_PROGRESS: "bg-indigo-100 text-indigo-700",
  ORDERED: "bg-amber-100 text-amber-700",
  PENDING: "bg-amber-100 text-amber-700",
  DRAFT: "bg-amber-100 text-amber-700",
  PRELIMINARY: "bg-amber-100 text-amber-700",
  NOT_DISPENSED: "bg-amber-100 text-amber-700",
  COMPLETED: "bg-sky-100 text-sky-700",
  AMENDED: "bg-violet-100 text-violet-700",
  DISCHARGED: "bg-violet-100 text-violet-700",
  ARCHIVED: "bg-slate-100 text-slate-600",
  INACTIVE: "bg-slate-100 text-slate-600",
  NO_SHOW: "bg-slate-100 text-slate-600",
  CANCELLED: "bg-rose-100 text-rose-700",
  FAILED: "bg-rose-100 text-rose-700",
  LOCKED: "bg-rose-100 text-rose-700",
  CRITICAL: "bg-red-100 text-red-700",
  STAT: "bg-red-100 text-red-700",
  URGENT: "bg-orange-100 text-orange-700",
  ROUTINE: "bg-slate-100 text-slate-600",
};

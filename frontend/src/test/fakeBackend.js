// Test double that answers with the SAME JSON shapes as the Spring controllers
// (wrapped {success, <key>: ...} for some modules, raw arrays/objects for others).
// It exists only for the smoke test; it is not used by the app.
const ok = (extra) => ({ success: true, ...extra });

export const USERS = {
  PATIENT: { userId: 7, email: "patient@hospital.local", firstName: "Peter", lastName: "Walker", roles: ["PATIENT"] },
  DOCTOR: { userId: 2, email: "doctor@hospital.local", firstName: "Daniel", lastName: "Carter", roles: ["DOCTOR"] },
  NURSE: { userId: 4, email: "nurse@hospital.local", firstName: "Nora", lastName: "Bennett", roles: ["NURSE"] },
  LAB_TECHNICIAN: { userId: 5, email: "lab@hospital.local", firstName: "Liam", lastName: "Foster", roles: ["LAB_TECHNICIAN"] },
  PHARMACIST: { userId: 6, email: "pharmacist@hospital.local", firstName: "Paula", lastName: "Reyes", roles: ["PHARMACIST"] },
  ADMIN: { userId: 1, email: "admin@hospital.local", firstName: "Ava", lastName: "Admin", roles: ["ADMIN"] },
};

const patient = { id: 1, userId: 7, patientNumber: "PAT-2026-000001", firstName: "Peter", lastName: "Walker", email: "patient@hospital.local", phone: "999", dateOfBirth: "1990-04-12", gender: "MALE", bloodGroup: "O+", address: "12 Green St", allergies: "Penicillin", version: 0, createdAt: "2026-09-01T10:00:00", updatedAt: "2026-09-01T10:00:00" };
const appointment = { id: 11, patientId: 1, doctorId: 2, departmentId: 1, appointmentDate: "2030-01-15", startTime: "09:00:00", endTime: "09:30:00", reason: "Checkup", status: "SCHEDULED", createdAt: "2026-09-01T10:00:00" };
const rx = { id: 21, patientId: 1, doctorId: 2, diagnosisCode: "J45.9", prescriptionDate: "2026-09-10", status: "SIGNED", dispenseStatus: "NOT_DISPENSED", doctorSignatureHash: "abc", expiresAt: "2027-01-01T00:00:00" };
const item = { id: 1, prescriptionId: 21, drugId: 1, brandName: "Crocin", genericName: "Paracetamol", dosageForm: "Tablet", strength: "500 mg", frequency: "Twice daily", durationDays: 5 };
const drug = { id: 1, brandName: "Crocin", genericName: "Paracetamol", dosageForm: "Tablet", strength: "500 mg", active: true };
const disp = { id: 31, prescriptionId: 21, pharmacistId: 6, dispensedAt: "2026-09-11T10:00:00", status: "DISPENSED", notes: "ok" };
const lab = { id: 41, patientId: 1, doctorId: 2, testName: "CBC", testCode: "CBC", priority: "ROUTINE", status: "RESULT_AVAILABLE", orderedAt: "2026-09-10T09:00:00" };
const labResult = { id: 51, labOrderId: 41, labTechnicianId: 5, resultValue: "13.2", unit: "g/dL", referenceRange: "12-16", resultStatus: "PRELIMINARY", performedAt: "2026-09-10T11:00:00" };
const adm = { id: 61, patientId: 1, admittingDoctorId: 2, bedNumber: "B-12", admissionCategory: "GENERAL", status: "ADMITTED", admissionDate: "2026-09-12T08:00:00" };
const vital = { id: 71, patientId: 1, temperature: 36.8, bpSystolic: 120, bpDiastolic: 80, pulseRate: 72, spo2: 98, respiratoryRate: 16, recordedAt: "2026-09-12T09:00:00" };
const vital2 = { ...vital, id: 72, pulseRate: 80, recordedAt: "2026-09-13T09:00:00" };
const rad = { id: 81, patientId: 1, orderingDoctorId: 2, modality: "XRAY", clinicalIndication: "Cough", urgency: "ROUTINE", status: "ORDERED", createdAt: "2026-09-12T09:00:00" };
const study = { id: 91, radiologyOrderId: 81, accessionNumber: "ACC-1", imageCount: 2, storageType: "PACS", studyStatus: "AVAILABLE", viewerUrl: "https://viewer.example/1" };
const report = { id: 101, radiologyOrderId: 81, reportedByUserId: 2, reportText: "Clear", findings: "None", impression: "Normal", criticalFinding: false, status: "DRAFT" };
const notif = { id: 111, userId: 7, type: "INFO", title: "Welcome", message: "Hello", read: false, createdAt: "2026-09-12T09:00:00" };
const userRow = { userId: 7, email: "patient@hospital.local", firstName: "Peter", lastName: "Walker", roles: ["PATIENT"], accountStatus: "ACTIVE" };
const audit = { id: 121, userId: 1, patientId: 1, action: "VIEW", resourceType: "PATIENT", resourceId: 1, description: "Viewed", ipAddress: "127.0.0.1", createdAt: "2026-09-12T09:00:00" };

/** [method, regexp, payload | fn(match, config)] */
const routes = [
  ["get", /^\/users\/me$/, (m, c, role) => ok({ ...USERS[role], phone: "1", emailVerified: true, accountStatus: "ACTIVE" })],
  ["get", /^\/patients\/me$/, () => ok({ patient })],
  ["get", /^\/patients\/number\/.+$/, () => ok({ patient })],
  ["get", /^\/patients\/\d+$/, () => ok({ patient })],
  ["get", /^\/patients$/, () => ok({ count: 1, patients: [patient] })],
  ["get", /^\/directory\/doctors$/, () => [{ userId: 2, firstName: "Daniel", lastName: "Carter", departmentId: 1 }]],
  ["get", /^\/directory\/departments$/, () => [{ id: 1, code: "GEN", name: "General Medicine" }]],
  ["get", /^\/doctors\/me\/patients$/, () => [patient]],
  ["get", /^\/appointments\/me$/, () => ok({ count: 1, appointments: [appointment] })],
  ["get", /^\/appointments\/date\/.+$/, () => ok({ appointments: [appointment] })],
  ["get", /^\/appointments\/doctor\/\d+\/date\/.+$/, () => ok({ appointments: [appointment] })],
  ["get", /^\/appointments\/\d+$/, () => ok({ appointment })],
  ["get", /^\/appointments$/, () => ok({ appointments: [appointment] })],
  ["get", /^\/prescriptions\/patient\/\d+$/, () => ok({ prescriptions: [rx] })],
  ["get", /^\/prescriptions\/doctor\/\d+$/, () => ok({ prescriptions: [{ ...rx, status: "DRAFT" }] })],
  ["get", /^\/prescriptions\/\d+\/items$/, () => ok({ items: [item] })],
  ["get", /^\/prescriptions\/\d+$/, () => ok({ prescription: rx })],
  ["get", /^\/drugs\/search\/.+$/, () => ok({ drugs: [drug] })],
  ["get", /^\/drugs$/, () => ok({ drugs: [drug] })],
  ["get", /^\/pharmacy\/dispensations\/status\/.+$/, () => ok({ dispensations: [disp] })],
  ["get", /^\/pharmacy\/dispensations\/\d+$/, () => ok({ dispensation: disp })],
  ["get", /^\/pharmacy\/pharmacists\/\d+\/dispensations$/, () => ok({ dispensations: [disp] })],
  ["get", /^\/pharmacy\/prescriptions\/\d+\/dispensations$/, () => ok({ dispensations: [disp] })],
  ["get", /^\/laboratory\/orders\/status\/.+$/, () => ok({ labOrders: [lab] })],
  ["get", /^\/laboratory\/orders\/\d+\/results$/, () => ok({ labResults: [labResult] })],
  ["get", /^\/laboratory\/orders\/\d+$/, () => ok({ labOrder: lab })],
  ["get", /^\/laboratory\/(patients|doctors)\/\d+\/orders$/, () => ok({ labOrders: [lab] })],
  ["get", /^\/laboratory\/technicians\/\d+\/results$/, () => ok({ labResults: [labResult] })],
  ["get", /^\/inpatient\/admissions\/(status|category)\/.+$/, () => [adm]],
  ["get", /^\/inpatient\/admissions\/\d+$/, () => adm],
  ["get", /^\/inpatient\/(patients|doctors)\/\d+\/admissions$/, () => [adm]],
  ["get", /^\/vitals\/patients\/\d+\/latest$/, () => vital2],
  ["get", /^\/vitals\/patients\/\d+$/, () => [vital, vital2]],
  ["get", /^\/radiology\/orders\/(status|modality|urgency)\/.+$/, () => [rad]],
  ["get", /^\/radiology\/orders\/(patients|doctors)\/\d+$/, () => [rad]],
  ["get", /^\/radiology\/orders\/\d+$/, () => rad],
  ["get", /^\/radiology\/studies\/orders\/\d+$/, () => [study]],
  ["get", /^\/radiology\/studies\/\d+$/, () => study],
  ["get", /^\/radiology\/studies\/accession\/.+$/, () => study],
  ["get", /^\/radiology\/studies\/status\/.+$/, () => [study]],
  ["get", /^\/radiology\/studies\/\d+\/files$/, () => [{ id: 201, studyId: 91, originalFileName: "scan.png", contentType: "image/png", sizeBytes: 1234, uploadedAt: "2026-09-12T09:30:00" }]],
  ["get", /^\/radiology\/studies\/\d+\/files\/\d+\/download$/, () => ({ ok: true })],
  ["post", /^\/radiology\/studies\/\d+\/files$/, () => ({ id: 201, studyId: 91, originalFileName: "scan.png", contentType: "image/png", sizeBytes: 1234, uploadedAt: "2026-09-12T09:30:00" })],
  ["get", /^\/radiology\/reports\/orders\/\d+$/, () => report],
  ["get", /^\/radiology\/reports\/(status\/.+|critical|users\/\d+)$/, () => [report]],
  ["get", /^\/notifications\/me\/unread\/count$/, () => 1],
  ["get", /^\/notifications\/me(\/unread)?$/, () => [notif]],
  ["get", /^\/admin\/users(\/role\/.+)?$/, () => [userRow, { ...userRow, userId: 2, roles: ["DOCTOR"], firstName: "Daniel", lastName: "Carter" }]],
  ["get", /^\/audit-logs\/.+$/, () => [audit]],
  ["get", /^\/dashboard\/admin$/, () => ok({ role: "ADMIN", dashboard: "ADMIN_DASHBOARD", statistics: { totalUsers: 7, totalPatients: 1, todayAppointments: 2, activeAdmissions: 1, pendingLabOrders: 1, pendingPrescriptions: 1, pendingRadiologyOrders: 1 } })],
];

export const createAdapter = (role, log = []) => async (config) => {
  const url = config.url.replace(config.baseURL || "", "");
  const method = (config.method || "get").toLowerCase();
  log.push(`${method.toUpperCase()} ${url}`);
  const hit = routes.find(([m, re]) => m === method && re.test(url));
  if (!hit) {
    log.push(`MISS ${method.toUpperCase()} ${url}`);
    const error = new Error(`Unmocked ${method.toUpperCase()} ${url}`);
    error.response = { status: 404, data: { message: `Unmocked ${method.toUpperCase()} ${url}` }, config };
    error.config = config;
    throw error;
  }
  return { data: hit[2](url.match(hit[1]), config, role), status: 200, statusText: "OK", headers: {}, config };
};

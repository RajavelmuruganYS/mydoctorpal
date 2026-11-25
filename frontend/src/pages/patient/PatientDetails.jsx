import { useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { Pencil, Trash2 } from "lucide-react";
import PageHeader from "../../components/common/PageHeader";
import Async from "../../components/common/Async";
import DetailGrid from "../../components/common/DetailGrid";
import ConfirmDialog from "../../components/common/ConfirmDialog";
import Button from "../../components/common/Button";
import { BackLink } from "../../components/common/RecordStates";
import { useApi } from "../../hooks/useApi";
import { useMutation } from "../../hooks/useMutation";
import { useAuth } from "../../context/AuthContext";
import { patientApi } from "../../api/patientApi";
import { adminApi } from "../../api/adminApi";
import { directoryApi } from "../../api/directoryApi";
import Select from "../../components/common/Select";
import { personName } from "../../utils/payload";
import { formatDate, formatDateTime } from "../../utils/formatDate";

const LINKS = [
  { label: "Appointments", to: "/app/appointments", roles: ["DOCTOR", "NURSE", "ADMIN"] },
  { label: "Vitals", to: "/app/vitals", roles: ["DOCTOR", "NURSE", "ADMIN"] },
  { label: "Prescriptions", to: "/app/prescriptions", roles: ["DOCTOR", "NURSE", "PHARMACIST", "ADMIN"] },
  { label: "Lab orders", to: "/app/laboratory/orders", roles: ["DOCTOR", "NURSE", "LAB_TECHNICIAN", "ADMIN"] },
  { label: "Radiology", to: "/app/radiology/orders", roles: ["DOCTOR", "NURSE", "RADIOLOGY_TECHNICIAN", "ADMIN"] },
  { label: "Admissions", to: "/app/inpatient", roles: ["DOCTOR", "NURSE", "ADMIN"] },
];

export function PatientRecord({ patient }) {
  return (
    <DetailGrid
      items={[
        { label: "Patient number", value: patient.patientNumber },
        { label: "Email", value: patient.email },
        { label: "Phone", value: patient.phone },
        { label: "Date of birth", value: formatDate(patient.dateOfBirth) },
        { label: "Gender", value: patient.gender },
        { label: "Blood group", value: patient.bloodGroup },
        { label: "Address", value: patient.address },
        { label: "Emergency contact", value: [patient.emergencyContactName, patient.emergencyContactPhone].filter(Boolean).join(" · ") },
        { label: "Record created", value: formatDateTime(patient.createdAt) },
        { label: "Allergies", value: patient.allergies },
        { label: "Medical history", value: patient.medicalHistory },
        { label: "Last updated", value: formatDateTime(patient.updatedAt) },
      ]}
    />
  );
}

export default function PatientDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { hasRole } = useAuth();
  const [confirm, setConfirm] = useState(false);
  const state = useApi(() => patientApi.get(id), [id]);
  const remove = useMutation(patientApi.remove, { success: "Patient record deleted." });
  const assignments = useApi(() => adminApi.assignedDoctors(id), [id], hasRole("ADMIN"));
  const doctors = useApi(() => directoryApi.doctors(), [], hasRole("ADMIN"));
  const assign = useMutation((doctorId) => adminApi.assignDoctor(id, doctorId), { success: "Doctor assigned." });
  const revoke = useMutation((doctorId) => adminApi.revokeDoctor(id, doctorId), { success: "Doctor assignment revoked." });

  const refreshAssignments = async () => {
    await assignments.reload();
  };

  const doDelete = async () => {
    if (await remove.run(id)) navigate("/app/patients", { replace: true });
  };

  return (
    <>
      <BackLink to="/app/patients">All patients</BackLink>
      <Async state={state}>
        {(patient) => (
          <>
            <PageHeader
              eyebrow="Patient"
              title={personName(patient) || patient.patientNumber}
              description={`Patient number ${patient.patientNumber}`}
              actions={
                <>
                  {hasRole("DOCTOR", "NURSE", "ADMIN") && (
                    <Link to={`/app/patients/${id}/edit`} className="btn-secondary">
                      <Pencil size={15} /> Edit
                    </Link>
                  )}
                  {hasRole("ADMIN") && (
                    <Button variant="danger" onClick={() => setConfirm(true)}>
                      <Trash2 size={15} /> Delete
                    </Button>
                  )}
                </>
              }
            />
            <div className="surface p-6">
              <PatientRecord patient={patient} />
            </div>
            <div className="surface mt-6 p-6">
              <h3 className="mb-4 font-bold">Clinical records for this patient</h3>
              <div className="flex flex-wrap gap-3">
                {LINKS.filter((l) => hasRole(l.roles)).map((l) => (
                  <Link key={l.to} to={`${l.to}?patient=${id}`} className="btn-secondary">
                    {l.label}
                  </Link>
                ))}
              </div>
            </div>
            {hasRole("ADMIN") && <div className="surface mt-6 p-6">
              <h3 className="mb-4 font-bold">Assigned doctors</h3>
              <div className="mb-4 space-y-2">
                {(assignments.data || []).filter((a) => a.status === "ACTIVE").map((a) => <div key={a.id} className="flex items-center justify-between rounded-xl border border-forest/10 p-3 text-sm">
                  <span>{a.doctorName}</span><Button variant="secondary" onClick={async () => { if (await revoke.run(a.doctorId)) await refreshAssignments(); }}>Revoke</Button>
                </div>)}
                {!assignments.loading && !(assignments.data || []).length && <p className="text-sm muted">No doctors assigned.</p>}
              </div>
              <Select label="Assign doctor" value="" onChange={async (e) => { if (e.target.value && await assign.run(Number(e.target.value))) assignments.reload(); }}>
                <option value="">Select a doctor…</option>
                {(doctors.data || []).filter((d) => !(assignments.data || []).some((a) => a.status === "ACTIVE" && a.doctorId === d.userId)).map((d) => <option key={d.userId} value={d.userId}>Dr. {d.firstName} {d.lastName}</option>)}
              </Select>
            </div>}
            <ConfirmDialog
              open={confirm}
              danger
              loading={remove.loading}
              title="Delete patient record?"
              message="This permanently removes the patient record. Records that reference it may prevent deletion."
              confirmLabel="Delete"
              onClose={() => setConfirm(false)}
              onConfirm={doDelete}
            />
          </>
        )}
      </Async>
    </>
  );
}

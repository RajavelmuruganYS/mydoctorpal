import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import PageHeader from "../../components/common/PageHeader";
import Async from "../../components/common/Async";
import DetailGrid from "../../components/common/DetailGrid";
import StatusBadge from "../../components/common/StatusBadge";
import Button from "../../components/common/Button";
import Select from "../../components/common/Select";
import Modal from "../../components/common/Modal";
import Textarea from "../../components/common/Textarea";
import ConfirmDialog from "../../components/common/ConfirmDialog";
import { BackLink } from "../../components/common/RecordStates";
import { useApi } from "../../hooks/useApi";
import { useMutation } from "../../hooks/useMutation";
import { useDirectory } from "../../hooks/useDirectory";
import { useAuth } from "../../context/AuthContext";
import { appointmentApi } from "../../api/appointmentApi";
import { formatDate, formatDateTime, formatTime } from "../../utils/formatDate";
import { APPOINTMENT_STATUSES } from "../../utils/constants";

export default function AppointmentDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { hasRole, isPatient } = useAuth();
  const dir = useDirectory();
  const state = useApi(() => appointmentApi.get(id), [id]);
  const [nextStatus, setNextStatus] = useState("");
  const [cancelOpen, setCancelOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [reason, setReason] = useState("");

  const updateStatus = useMutation((status) => appointmentApi.status(id, status), { success: "Status updated." });
  const cancel = useMutation((r) => appointmentApi.cancel(id, r), { success: "Appointment cancelled." });
  const remove = useMutation(() => appointmentApi.remove(id), { success: "Appointment deleted." });

  const applyStatus = async () => {
    if (nextStatus && (await updateStatus.run(nextStatus))) {
      setNextStatus("");
      state.reload();
    }
  };
  const doCancel = async () => {
    if (await cancel.run(reason.trim() || undefined)) {
      setCancelOpen(false);
      setReason("");
      state.reload();
    }
  };
  const doDelete = async () => {
    if (await remove.run()) navigate("/app/appointments", { replace: true });
  };

  return (
    <>
      <BackLink to="/app/appointments">Appointments</BackLink>
      <Async state={state}>
        {(a) => {
          const open = !["CANCELLED", "COMPLETED", "NO_SHOW"].includes(a.status);
          return (
            <>
              <PageHeader
                eyebrow="Appointment"
                title={`${formatDate(a.appointmentDate)} · ${formatTime(a.startTime)}`}
                description={a.reason || undefined}
                actions={<StatusBadge status={a.status} />}
              />
              <div className="surface p-6">
                <DetailGrid
                  items={[
                    { label: "Patient", value: isPatient ? "You" : dir.patientName(a.patientId) },
                    { label: "Doctor", value: dir.doctorName(a.doctorId) },
                    { label: "Department", value: dir.departmentName(a.departmentId) },
                    { label: "Date", value: formatDate(a.appointmentDate) },
                    { label: "Time", value: `${formatTime(a.startTime)} – ${formatTime(a.endTime)}` },
                    { label: "Booked", value: formatDateTime(a.createdAt) },
                    { label: "Notes", value: a.notes },
                    { label: "Cancellation reason", value: a.cancellationReason },
                  ]}
                />
              </div>
              <div className="surface mt-6 flex flex-wrap items-end gap-4 p-6">
                {hasRole("ADMIN", "DOCTOR", "NURSE") && (
                  <>
                    <Select label="Change status" value={nextStatus} onChange={(e) => setNextStatus(e.target.value)} className="w-56">
                      <option value="">Select status…</option>
                      {APPOINTMENT_STATUSES.filter((s) => s !== a.status && s !== "CANCELLED").map((s) => (
                        <option key={s} value={s}>{s.replaceAll("_", " ")}</option>
                      ))}
                    </Select>
                    <Button onClick={applyStatus} loading={updateStatus.loading} disabled={!nextStatus}>Update status</Button>
                  </>
                )}
                {open && hasRole("ADMIN", "DOCTOR", "PATIENT") && (
                  <Button variant="secondary" onClick={() => setCancelOpen(true)}>Cancel appointment</Button>
                )}
                {hasRole("ADMIN") && (
                  <Button variant="danger" onClick={() => setDeleteOpen(true)}>Delete</Button>
                )}
              </div>
              <Modal open={cancelOpen} onClose={() => setCancelOpen(false)} title="Cancel appointment">
                <Textarea label="Reason (optional)" maxLength={500} value={reason} onChange={(e) => setReason(e.target.value)} />
                <div className="mt-5 flex justify-end gap-3">
                  <Button variant="secondary" onClick={() => setCancelOpen(false)}>Keep appointment</Button>
                  <Button variant="danger" loading={cancel.loading} onClick={doCancel}>Cancel appointment</Button>
                </div>
              </Modal>
              <ConfirmDialog
                open={deleteOpen}
                danger
                loading={remove.loading}
                title="Delete appointment?"
                message="This permanently removes the appointment record."
                confirmLabel="Delete"
                onClose={() => setDeleteOpen(false)}
                onConfirm={doDelete}
              />
            </>
          );
        }}
      </Async>
    </>
  );
}

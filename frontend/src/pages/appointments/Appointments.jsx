import { useState } from "react";
import { Link } from "react-router-dom";
import { Plus } from "lucide-react";
import PageHeader from "../../components/common/PageHeader";
import DataTable from "../../components/common/DataTable";
import EmptyState from "../../components/common/EmptyState";
import Async from "../../components/common/Async";
import Segmented from "../../components/common/Segmented";
import Input from "../../components/common/Input";
import StatusBadge from "../../components/common/StatusBadge";
import { NoPatientRecord } from "../../components/common/RecordStates";
import { useApi } from "../../hooks/useApi";
import { useDirectory } from "../../hooks/useDirectory";
import { useAuth } from "../../context/AuthContext";
import { appointmentApi } from "../../api/appointmentApi";
import { asList } from "../../utils/payload";
import { formatDate, formatTime, todayISO } from "../../utils/formatDate";
import { APPOINTMENT_STATUSES } from "../../utils/constants";

export default function Appointments() {
  const { role, isPatient, patientId, hasRole } = useAuth();
  const dir = useDirectory();
  const [status, setStatus] = useState("ALL");
  const [date, setDate] = useState(role === "NURSE" ? todayISO() : "");

  // Nurses have no "all appointments" endpoint, they browse by date.
  const byDate = role === "NURSE";
  const state = useApi(() => {
    if (byDate) return appointmentApi.byDate(date || todayISO());
    if (role === "ADMIN") return appointmentApi.list();
    return appointmentApi.mine(); // doctor: own schedule, patient: own bookings
  }, [role, byDate, date], !isPatient || Boolean(patientId));

  if (isPatient && !patientId) return <NoPatientRecord />;

  return (
    <>
      <PageHeader
        eyebrow="Appointments"
        title={isPatient ? "My appointments" : role === "DOCTOR" ? "My schedule" : "Appointments"}
        description={byDate ? "Appointments for the selected day." : undefined}
        actions={
          hasRole("PATIENT", "ADMIN") && (
            <Link to="/app/appointments/new" className="btn-primary">
              <Plus size={16} /> Book appointment
            </Link>
          )
        }
      />
      <div className="surface p-5">
        <div className="mb-4 flex flex-wrap items-end gap-4">
          <Segmented options={["ALL", ...APPOINTMENT_STATUSES]} value={status} onChange={setStatus} />
          {(byDate || role === "ADMIN" || role === "DOCTOR") && (
            <Input label={byDate ? "Day" : "Filter by day"} type="date" value={date} onChange={(e) => setDate(e.target.value)} />
          )}
        </div>
        <Async state={state}>
          {(data) => {
            const rows = asList(data)
              .filter((a) => status === "ALL" || a.status === status)
              .filter((a) => byDate || !date || a.appointmentDate === date)
              .sort((a, b) => `${b.appointmentDate}${b.startTime}`.localeCompare(`${a.appointmentDate}${a.startTime}`));
            return (
              <DataTable
                rows={rows}
                rowLink={(a) => `/app/appointments/${a.id}`}
                empty={<EmptyState title="No appointments" message="No appointments match these filters." />}
                columns={[
                  { key: "appointmentDate", label: "Date", render: (a) => formatDate(a.appointmentDate) },
                  { key: "time", label: "Time", render: (a) => `${formatTime(a.startTime)} – ${formatTime(a.endTime)}` },
                  ...(isPatient ? [] : [{ key: "patient", label: "Patient", render: (a) => dir.patientName(a.patientId) }]),
                  ...(role === "DOCTOR" ? [] : [{ key: "doctor", label: "Doctor", render: (a) => dir.doctorName(a.doctorId) }]),
                  { key: "department", label: "Department", render: (a) => dir.departmentName(a.departmentId) },
                  { key: "reason", label: "Reason" },
                  { key: "status", label: "Status", render: (a) => <StatusBadge status={a.status} /> },
                ]}
              />
            );
          }}
        </Async>
      </div>
    </>
  );
}

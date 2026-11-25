import { useAuth } from "../../context/AuthContext";
import { useDirectory } from "../../hooks/useDirectory";
import { NoPatientRecord } from "../../components/common/RecordStates";
import StatusBadge from "../../components/common/StatusBadge";
import { appointmentApi } from "../../api/appointmentApi";
import { prescriptionApi } from "../../api/prescriptionApi";
import { laboratoryApi } from "../../api/laboratoryApi";
import { notificationApi } from "../../api/notificationApi";
import { radiologyApi } from "../../api/radiologyApi";
import { vitalsApi } from "../../api/vitalsApi";
import { inpatientApi } from "../../api/inpatientApi";
import { asList, personName } from "../../utils/payload";
import { formatDate, formatTime, todayISO } from "../../utils/formatDate";
import { DashboardShell, MiniTable, Panel, count, useDashboardData } from "./dashboardKit";

export default function PatientDashboard() {
  const { user, patient, patientId } = useAuth();
  const dir = useDirectory();
  const data = useDashboardData(() => ({
    appointments: () => appointmentApi.mine(),
    prescriptions: () => prescriptionApi.byPatient(patientId),
    labs: () => laboratoryApi.patientOrders(patientId),
    imaging: () => radiologyApi.patientOrders(patientId),
    vitals: () => vitalsApi.latest(patientId),
    admissions: () => inpatientApi.byPatient(patientId),
    notifications: () => notificationApi.mine(),
  }), [patientId], Boolean(patientId));

  if (!patientId) return <NoPatientRecord />;
  const d = data.data || {};
  const today = todayISO();
  const upcoming = asList(d.appointments).filter((a) => a.appointmentDate >= today && !["CANCELLED", "COMPLETED", "NO_SHOW"].includes(a.status));
  const v = d.vitals;

  return (
    <DashboardShell
      title={`Hello, ${personName(user) || personName(patient) || "there"}`}
      description="Your care at a glance."
      data={data}
      stats={[
        { label: "Upcoming appointments", value: d.appointments ? upcoming.length : null, to: "/app/appointments" },
        { label: "Prescriptions", value: count(d.prescriptions), to: "/app/prescriptions" },
        { label: "Lab orders", value: count(d.labs), to: "/app/laboratory/orders" },
        { label: "Radiology orders", value: count(d.imaging), to: "/app/radiology/orders" },
        { label: "Latest blood pressure", value: v?.bpSystolic != null ? `${v.bpSystolic}/${v.bpDiastolic ?? "–"}` : "—", hint: v?.pulseRate != null ? `Pulse ${v.pulseRate} bpm` : "No vitals recorded", to: "/app/vitals" },
      ]}
    >
      <div className="grid gap-6 lg:grid-cols-2">
        <Panel title="Upcoming appointments" to="/app/appointments">
          <MiniTable
            rows={upcoming}
            rowLink={(a) => `/app/appointments/${a.id}`}
            empty="No upcoming appointments"
            columns={[
              { key: "d", label: "When", render: (a) => `${formatDate(a.appointmentDate)} ${formatTime(a.startTime)}` },
              { key: "doctor", label: "Doctor", render: (a) => dir.doctorName(a.doctorId) },
              { key: "status", label: "Status", render: (a) => <StatusBadge status={a.status} /> },
            ]}
          />
        </Panel>
        <Panel title="Recent prescriptions" to="/app/prescriptions">
          <MiniTable
            rows={d.prescriptions}
            rowLink={(p) => `/app/prescriptions/${p.id}`}
            empty="No prescriptions"
            columns={[
              { key: "id", label: "Prescription", render: (p) => `#${p.id}` },
              { key: "date", label: "Date", render: (p) => formatDate(p.prescriptionDate) },
              { key: "status", label: "Status", render: (p) => <StatusBadge status={p.dispenseStatus} /> },
            ]}
          />
        </Panel>
        <Panel title="Lab orders" to="/app/laboratory/orders">
          <MiniTable
            rows={d.labs}
            rowLink={(o) => `/app/laboratory/orders/${o.id}`}
            empty="No lab orders"
            columns={[
              { key: "testName", label: "Test" },
              { key: "status", label: "Status", render: (o) => <StatusBadge status={o.status} /> },
            ]}
          />
        </Panel>
        <Panel title="Radiology" to="/app/radiology/orders">
          <MiniTable
            rows={d.imaging}
            rowLink={(o) => `/app/radiology/orders/${o.id}`}
            empty="No radiology orders"
            columns={[
              { key: "modality", label: "Modality" },
              { key: "urgency", label: "Urgency", render: (o) => <StatusBadge status={o.urgency} /> },
              { key: "status", label: "Status", render: (o) => <StatusBadge status={o.status} /> },
            ]}
          />
        </Panel>
        <Panel title="Admissions" to="/app/inpatient">
          <MiniTable
            rows={d.admissions}
            rowLink={(a) => `/app/inpatient/${a.id}`}
            empty="No admissions"
            columns={[
              { key: "id", label: "Admission", render: (a) => `#${a.id}` },
              { key: "admissionCategory", label: "Category" },
              { key: "status", label: "Status", render: (a) => <StatusBadge status={a.status} /> },
            ]}
          />
        </Panel>
        <Panel title="Notifications" to="/app/notifications">
          <MiniTable
            rows={d.notifications}
            empty="No notifications"
            columns={[
              { key: "title", label: "Subject" },
              { key: "message", label: "Message" },
              { key: "read", label: "Status", render: (n) => <StatusBadge status={n.read ? "READ" : "UNREAD"} /> },
            ]}
          />
        </Panel>
      </div>
    </DashboardShell>
  );
}

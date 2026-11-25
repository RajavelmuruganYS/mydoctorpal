import { useAuth } from "../../context/AuthContext";
import { useDirectory } from "../../hooks/useDirectory";
import StatusBadge from "../../components/common/StatusBadge";
import { appointmentApi } from "../../api/appointmentApi";
import { directoryApi } from "../../api/directoryApi";
import { prescriptionApi } from "../../api/prescriptionApi";
import { laboratoryApi } from "../../api/laboratoryApi";
import { radiologyApi } from "../../api/radiologyApi";
import { inpatientApi } from "../../api/inpatientApi";
import { asList, personName } from "../../utils/payload";
import { formatDate, formatTime, todayISO } from "../../utils/formatDate";
import { DashboardShell, MiniTable, Panel, count, useDashboardData } from "./dashboardKit";

export default function DoctorDashboard() {
  const { user, userId } = useAuth();
  const dir = useDirectory();
  const today = todayISO();
  const data = useDashboardData(() => ({
    today: () => appointmentApi.byDoctorDate(userId, today),
    patients: () => directoryApi.assignedPatients(),
    prescriptions: () => prescriptionApi.byDoctor(userId),
    labs: () => laboratoryApi.doctorOrders(userId),
    imaging: () => radiologyApi.doctorOrders(userId),
    admissions: () => inpatientApi.byDoctor(userId),
  }), [userId, today]);
  const d = data.data || {};
  const drafts = asList(d.prescriptions).filter((p) => p.status === "DRAFT");
  const pendingLabs = asList(d.labs).filter((o) => o.status === "ORDERED");
  const admitted = asList(d.admissions).filter((a) => a.status === "ADMITTED");

  return (
    <DashboardShell
      title={`Good day, Dr. ${user?.lastName || user?.firstName || ""}`}
      description={`Your schedule and open items for ${formatDate(today)}.`}
      data={data}
      stats={[
        { label: "Appointments today", value: count(d.today), to: "/app/appointments" },
        { label: "Patients", value: count(d.patients), to: "/app/patients" },
        { label: "Draft prescriptions", value: d.prescriptions ? drafts.length : null, hint: "Waiting for your signature", to: "/app/prescriptions" },
        { label: "Admitted patients", value: d.admissions ? admitted.length : null, to: "/app/inpatient" },
        { label: "Lab orders awaiting result", value: d.labs ? pendingLabs.length : null, to: "/app/laboratory/orders" },
        { label: "Imaging orders", value: count(d.imaging), to: "/app/radiology/orders" },
      ]}
    >
      <div className="grid gap-6 lg:grid-cols-2">
        <Panel title="Today's appointments" to="/app/appointments">
          <MiniTable
            rows={asList(d.today).sort((a, b) => String(a.startTime).localeCompare(String(b.startTime)))}
            rowLink={(a) => `/app/appointments/${a.id}`}
            empty="No appointments today"
            columns={[
              { key: "time", label: "Time", render: (a) => formatTime(a.startTime) },
              { key: "patient", label: "Patient", render: (a) => dir.patientName(a.patientId) },
              { key: "reason", label: "Reason" },
              { key: "status", label: "Status", render: (a) => <StatusBadge status={a.status} /> },
            ]}
          />
        </Panel>
        <Panel title="Prescriptions to sign" to="/app/prescriptions">
          <MiniTable
            rows={drafts}
            rowLink={(p) => `/app/prescriptions/${p.id}`}
            empty="Nothing waiting for a signature"
            columns={[
              { key: "id", label: "Prescription", render: (p) => `#${p.id}` },
              { key: "patient", label: "Patient", render: (p) => dir.patientName(p.patientId) },
              { key: "date", label: "Date", render: (p) => formatDate(p.prescriptionDate) },
            ]}
          />
        </Panel>
        <Panel title="My admitted patients" to="/app/inpatient">
          <MiniTable
            rows={admitted}
            rowLink={(a) => `/app/inpatient/${a.id}`}
            empty="No admitted patients"
            columns={[
              { key: "patient", label: "Patient", render: (a) => dir.patientName(a.patientId) },
              { key: "bedNumber", label: "Bed" },
              { key: "admissionCategory", label: "Category" },
            ]}
          />
        </Panel>
        <Panel title="Lab results ready" to="/app/laboratory/orders">
          <MiniTable
            rows={asList(d.labs).filter((o) => o.status === "RESULT_AVAILABLE")}
            rowLink={(o) => `/app/laboratory/orders/${o.id}`}
            empty="No new results"
            columns={[
              { key: "testName", label: "Test" },
              { key: "patient", label: "Patient", render: (o) => dir.patientName(o.patientId) },
              { key: "status", label: "Status", render: (o) => <StatusBadge status={o.status} /> },
            ]}
          />
        </Panel>
      </div>
      <p className="mt-4 text-xs muted">Signed in as {personName(user)}.</p>
    </DashboardShell>
  );
}

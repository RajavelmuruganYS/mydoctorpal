import { useDirectory } from "../../hooks/useDirectory";
import StatusBadge from "../../components/common/StatusBadge";
import { appointmentApi } from "../../api/appointmentApi";
import { patientApi } from "../../api/patientApi";
import { inpatientApi } from "../../api/inpatientApi";
import { laboratoryApi } from "../../api/laboratoryApi";
import { radiologyApi } from "../../api/radiologyApi";
import { formatDate, formatTime, todayISO } from "../../utils/formatDate";
import { DashboardShell, MiniTable, Panel, count, useDashboardData } from "./dashboardKit";

export default function NurseDashboard() {
  const dir = useDirectory();
  const today = todayISO();
  const data = useDashboardData(() => ({
    today: () => appointmentApi.byDate(today),
    patients: () => patientApi.list(),
    admitted: () => inpatientApi.byStatus("ADMITTED"),
    labs: () => laboratoryApi.byStatus("ORDERED"),
    imaging: () => radiologyApi.ordersByStatus("ORDERED"),
  }), [today]);
  const d = data.data || {};

  return (
    <DashboardShell
      title="Ward overview"
      description={`Today is ${formatDate(today)}.`}
      data={data}
      stats={[
        { label: "Appointments today", value: count(d.today), to: "/app/appointments" },
        { label: "Admitted patients", value: count(d.admitted), to: "/app/inpatient" },
        { label: "Lab orders pending", value: count(d.labs), to: "/app/laboratory/orders" },
        { label: "Imaging orders pending", value: count(d.imaging), to: "/app/radiology/orders" },
        { label: "Registered patients", value: count(d.patients), to: "/app/patients" },
      ]}
    >
      <div className="grid gap-6 lg:grid-cols-2">
        <Panel title="Admitted patients" to="/app/inpatient">
          <MiniTable
            rows={d.admitted}
            rowLink={(a) => `/app/inpatient/${a.id}`}
            empty="No admitted patients"
            columns={[
              { key: "patient", label: "Patient", render: (a) => dir.patientName(a.patientId) },
              { key: "bedNumber", label: "Bed" },
              { key: "admissionCategory", label: "Category" },
            ]}
          />
        </Panel>
        <Panel title="Appointments today" to="/app/appointments">
          <MiniTable
            rows={d.today}
            rowLink={(a) => `/app/appointments/${a.id}`}
            empty="No appointments today"
            columns={[
              { key: "time", label: "Time", render: (a) => formatTime(a.startTime) },
              { key: "patient", label: "Patient", render: (a) => dir.patientName(a.patientId) },
              { key: "doctor", label: "Doctor", render: (a) => dir.doctorName(a.doctorId) },
              { key: "status", label: "Status", render: (a) => <StatusBadge status={a.status} /> },
            ]}
          />
        </Panel>
      </div>
    </DashboardShell>
  );
}

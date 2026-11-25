import { useAuth } from "../../context/AuthContext";
import { useDirectory } from "../../hooks/useDirectory";
import StatusBadge from "../../components/common/StatusBadge";
import { laboratoryApi } from "../../api/laboratoryApi";
import { formatDateTime } from "../../utils/formatDate";
import { DashboardShell, MiniTable, Panel, count, useDashboardData } from "./dashboardKit";

export default function LabDashboard() {
  const { userId } = useAuth();
  const dir = useDirectory();
  const data = useDashboardData(() => ({
    queue: () => laboratoryApi.byStatus("ORDERED"),
    done: () => laboratoryApi.byStatus("RESULT_AVAILABLE"),
    mine: () => laboratoryApi.technicianResults(userId),
  }), [userId]);
  const d = data.data || {};

  return (
    <DashboardShell
      title="Laboratory desk"
      description="Tests waiting for a result and your recent work."
      data={data}
      stats={[
        { label: "Awaiting result", value: count(d.queue), to: "/app/laboratory/orders" },
        { label: "Results available", value: count(d.done), to: "/app/laboratory/orders" },
        { label: "Results I entered", value: count(d.mine), to: "/app/laboratory/results" },
      ]}
    >
      <Panel title="Work queue" to="/app/laboratory/orders">
        <MiniTable
          rows={d.queue}
          rowLink={(o) => `/app/laboratory/orders/${o.id}`}
          empty="The queue is empty"
          columns={[
            { key: "testName", label: "Test" },
            { key: "patient", label: "Patient", render: (o) => dir.patientName(o.patientId) },
            { key: "priority", label: "Priority", render: (o) => <StatusBadge status={o.priority} /> },
            { key: "orderedAt", label: "Ordered", render: (o) => formatDateTime(o.orderedAt) },
          ]}
        />
      </Panel>
    </DashboardShell>
  );
}

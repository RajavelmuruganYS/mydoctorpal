import { Link } from "react-router-dom";
import { Plus } from "lucide-react";
import PageHeader from "../../components/common/PageHeader";
import DataTable from "../../components/common/DataTable";
import EmptyState from "../../components/common/EmptyState";
import Async from "../../components/common/Async";
import StatusBadge from "../../components/common/StatusBadge";
import { NoPatientRecord } from "../../components/common/RecordStates";
import { useApi } from "../../hooks/useApi";
import { useAuth } from "../../context/AuthContext";
import { useDirectory } from "../../hooks/useDirectory";
import { loadLabOrders } from "./labData";
import { asList } from "../../utils/payload";
import { formatDateTime } from "../../utils/formatDate";

function Count({ label, value, to }) {
  return (
    <Link to={to} className="surface block p-5 hover:shadow-soft">
      <p className="text-xs font-bold uppercase tracking-wider text-forest/50">{label}</p>
      <p className="mt-2 font-display text-4xl font-extrabold">{value ?? "—"}</p>
    </Link>
  );
}

export default function Laboratory() {
  const { role, userId, isPatient, patientId, hasRole } = useAuth();
  const dir = useDirectory();
  const queueRole = ["LAB_TECHNICIAN", "NURSE", "ADMIN"].includes(role);

  const pending = useApi(() => loadLabOrders({ isPatient, ownId: patientId, role, userId, status: "ORDERED" }), [role, userId, patientId], !isPatient || Boolean(patientId));
  const done = useApi(() => loadLabOrders({ isPatient, ownId: patientId, role, userId, status: "RESULT_AVAILABLE" }), [role, userId, patientId], queueRole);

  if (isPatient && !patientId) return <NoPatientRecord />;

  const orders = asList(pending.data);
  const awaiting = queueRole ? orders.length : orders.filter((o) => o.status === "ORDERED").length;
  const ready = queueRole ? asList(done.data).length : orders.filter((o) => o.status === "RESULT_AVAILABLE").length;

  return (
    <>
      <PageHeader
        eyebrow="Laboratory"
        title="Laboratory"
        description={isPatient ? "Your lab orders and results." : queueRole ? "Work queue: tests waiting for a result and completed tests." : "Lab orders you have placed."}
        actions={
          <>
            {hasRole("DOCTOR") && <Link to="/app/laboratory/orders/new" className="btn-primary"><Plus size={16} /> Order a test</Link>}
            <Link to="/app/laboratory/orders" className="btn-secondary">All orders</Link>
            <Link to="/app/laboratory/results" className="btn-secondary">Results</Link>
          </>
        }
      />
      <div className="mb-6 grid gap-4 sm:grid-cols-2">
        <Count label="Awaiting result" value={pending.loading ? "…" : awaiting} to="/app/laboratory/orders" />
        <Count label="Results available" value={pending.loading || done.loading ? "…" : ready} to="/app/laboratory/orders" />
      </div>
      <div className="surface p-5">
        <h3 className="mb-4 font-bold">{queueRole ? "Waiting for a result" : "Recent orders"}</h3>
        <Async state={pending}>
          {(data) => (
            <DataTable
              rows={asList(data).slice(0, 10)}
              rowLink={(o) => `/app/laboratory/orders/${o.id}`}
              empty={<EmptyState title="No lab orders" message="Nothing to show right now." />}
              columns={[
                { key: "testName", label: "Test" },
                ...(isPatient ? [] : [{ key: "patient", label: "Patient", render: (o) => dir.patientName(o.patientId) }]),
                { key: "priority", label: "Priority", render: (o) => <StatusBadge status={o.priority} /> },
                { key: "orderedAt", label: "Ordered", render: (o) => formatDateTime(o.orderedAt) },
                { key: "status", label: "Status", render: (o) => <StatusBadge status={o.status} /> },
              ]}
            />
          )}
        </Async>
      </div>
    </>
  );
}

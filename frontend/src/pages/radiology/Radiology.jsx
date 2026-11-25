import { Link } from "react-router-dom";
import { Plus } from "lucide-react";
import PageHeader from "../../components/common/PageHeader";
import Async from "../../components/common/Async";
import DataTable from "../../components/common/DataTable";
import EmptyState from "../../components/common/EmptyState";
import StatusBadge from "../../components/common/StatusBadge";
import { NoPatientRecord } from "../../components/common/RecordStates";
import { useApi } from "../../hooks/useApi";
import { useAuth } from "../../context/AuthContext";
import { useDirectory } from "../../hooks/useDirectory";
import { radiologyApi } from "../../api/radiologyApi";
import { asList } from "../../utils/payload";
import { formatDateTime } from "../../utils/formatDate";
import { RADIOLOGY_ORDER_STATUSES } from "../../utils/constants";

export default function Radiology() {
  const { role, userId, isPatient, patientId, hasRole } = useAuth();
  const dir = useDirectory();
  const staffQueue = ["NURSE", "ADMIN", "LAB_TECHNICIAN", "RADIOLOGY_TECHNICIAN"].includes(role);

  // Patients: own orders. Doctors: orders they placed. Nurses/admins: orders by status.
  const orders = useApi(async () => {
    if (isPatient) return radiologyApi.patientOrders(patientId);
    if (role === "DOCTOR") return radiologyApi.doctorOrders(userId);
    const lists = await Promise.all(RADIOLOGY_ORDER_STATUSES.map((s) => radiologyApi.ordersByStatus(s).then((r) => asList(r.data))));
    return { data: lists.flat() };
  }, [role, userId, patientId], !isPatient || Boolean(patientId));
  const critical = useApi(() => radiologyApi.criticalReports(), [], hasRole("DOCTOR", "ADMIN", "NURSE", "LAB_TECHNICIAN", "RADIOLOGY_TECHNICIAN"));

  if (isPatient && !patientId) return <NoPatientRecord />;

  const all = asList(orders.data);
  const count = (s) => all.filter((o) => o.status === s).length;

  return (
    <>
      <PageHeader
        eyebrow="Radiology"
        title="Radiology"
        description={isPatient ? "Your imaging orders and reports." : staffQueue ? "Imaging work across the hospital." : "Imaging you have ordered."}
        actions={
          <>
            {hasRole("DOCTOR") && <Link to="/app/radiology/orders/new" className="btn-primary"><Plus size={16} /> Order imaging</Link>}
            <Link to="/app/radiology/orders" className="btn-secondary">All orders</Link>
            {!isPatient && <Link to="/app/radiology/studies" className="btn-secondary">Studies</Link>}
            {!isPatient && <Link to="/app/radiology/reports" className="btn-secondary">Reports</Link>}
          </>
        }
      />
      <div className="mb-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {[
          ["Ordered", orders.loading ? "…" : count("ORDERED")],
          ["In progress", orders.loading ? "…" : count("IN_PROGRESS") + count("ARRIVED") + count("SCHEDULED")],
          ["Completed", orders.loading ? "…" : count("COMPLETED")],
          ...(!isPatient ? [["Critical findings", critical.loading ? "…" : asList(critical.data).length]] : []),
        ].map(([label, value]) => (
          <div key={label} className="surface p-5">
            <p className="text-xs font-bold uppercase tracking-wider text-forest/50">{label}</p>
            <p className="mt-2 font-display text-4xl font-extrabold">{value}</p>
          </div>
        ))}
      </div>
      <div className="surface p-5">
        <h3 className="mb-4 font-bold">Recent orders</h3>
        <Async state={orders}>
          {() => (
            <DataTable
              rows={[...all].sort((a, b) => String(b.createdAt).localeCompare(String(a.createdAt))).slice(0, 10)}
              rowLink={(o) => `/app/radiology/orders/${o.id}`}
              empty={<EmptyState title="No imaging orders" message="Nothing to show yet." />}
              columns={[
                { key: "id", label: "Order", render: (o) => `#${o.id}` },
                { key: "modality", label: "Modality" },
                ...(isPatient ? [] : [{ key: "patient", label: "Patient", render: (o) => o.patientName || dir.patientName(o.patientId) }]),
                { key: "urgency", label: "Urgency", render: (o) => <StatusBadge status={o.urgency} /> },
                { key: "createdAt", label: "Ordered", render: (o) => formatDateTime(o.createdAt) },
                { key: "status", label: "Status", render: (o) => <StatusBadge status={o.status} /> },
              ]}
            />
          )}
        </Async>
      </div>
    </>
  );
}

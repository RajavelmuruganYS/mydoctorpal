import { useState } from "react";
import { Link } from "react-router-dom";
import PageHeader from "../../components/common/PageHeader";
import DataTable from "../../components/common/DataTable";
import EmptyState from "../../components/common/EmptyState";
import Async from "../../components/common/Async";
import Select from "../../components/common/Select";
import StatusBadge from "../../components/common/StatusBadge";
import { NoPatientRecord } from "../../components/common/RecordStates";
import { useApi } from "../../hooks/useApi";
import { useAuth } from "../../context/AuthContext";
import { adminApi } from "../../api/adminApi";
import { laboratoryApi } from "../../api/laboratoryApi";
import { asList, personName } from "../../utils/payload";
import { formatDateTime } from "../../utils/formatDate";
import { loadLabOrders } from "./labData";

const resultColumns = [
  { key: "labOrderId", label: "Order", render: (r) => <Link className="font-bold underline" to={`/app/laboratory/orders/${r.labOrderId}`}>#{r.labOrderId}</Link> },
  { key: "resultValue", label: "Result", render: (r) => `${r.resultValue ?? "—"} ${r.unit ?? ""}` },
  { key: "referenceRange", label: "Reference" },
  { key: "interpretation", label: "Interpretation" },
  { key: "performedAt", label: "Performed", render: (r) => formatDateTime(r.performedAt) },
  { key: "resultStatus", label: "Status", render: (r) => <StatusBadge status={r.resultStatus} /> },
];

export default function LabResults() {
  const { role, userId, isPatient, patientId } = useAuth();
  const [technicianId, setTechnicianId] = useState("");
  const techs = useApi(() => adminApi.byRole("LAB_TECHNICIAN"), [], role === "ADMIN");

  // Lab technician / admin: results by technician.
  // Everyone else: results of the orders they can see.
  const byTechnician = role === "LAB_TECHNICIAN" || role === "ADMIN";
  const techId = role === "LAB_TECHNICIAN" ? userId : technicianId;

  const state = useApi(async () => {
    if (byTechnician) return laboratoryApi.technicianResults(techId);
    const orders = asList((await loadLabOrders({ isPatient, ownId: patientId, role, userId, status: "RESULT_AVAILABLE" })).data)
      .filter((o) => o.status === "RESULT_AVAILABLE")
      .slice(0, 50);
    const all = await Promise.all(orders.map((o) => laboratoryApi.orderResults(o.id).then((r) => asList(r.data))));
    return { data: all.flat() };
  }, [role, userId, patientId, techId], (!byTechnician && (!isPatient || Boolean(patientId))) || Boolean(techId));

  if (isPatient && !patientId) return <NoPatientRecord />;

  return (
    <>
      <PageHeader eyebrow="Laboratory" title={isPatient ? "My lab results" : "Lab results"} />
      {role === "ADMIN" && (
        <div className="surface mb-5 p-4">
          <Select label="Laboratory technician" value={technicianId} onChange={(e) => setTechnicianId(e.target.value)} className="max-w-md">
            <option value="">Select a technician…</option>
            {asList(techs.data).map((t) => <option key={t.userId} value={t.userId}>{personName(t)}</option>)}
          </Select>
        </div>
      )}
      {!(!byTechnician || techId) ? (
        <EmptyState title="Choose a technician" message="Select a technician to list the results they entered." />
      ) : (
        <div className="surface p-5">
          <Async state={state}>
            {(data) => <DataTable rows={asList(data)} columns={resultColumns} empty={<EmptyState title="No results yet" message="Completed lab results will appear here." />} />}
          </Async>
        </div>
      )}
    </>
  );
}

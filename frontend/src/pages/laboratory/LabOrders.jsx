import { useState } from "react";
import { Link } from "react-router-dom";
import { Plus } from "lucide-react";
import PageHeader from "../../components/common/PageHeader";
import DataTable from "../../components/common/DataTable";
import EmptyState from "../../components/common/EmptyState";
import Async from "../../components/common/Async";
import Segmented from "../../components/common/Segmented";
import StatusBadge from "../../components/common/StatusBadge";
import { PatientScopeBar, usePatientScope } from "../../components/common/Pickers";
import { NoPatientRecord } from "../../components/common/RecordStates";
import { useApi } from "../../hooks/useApi";
import { useAuth } from "../../context/AuthContext";
import { useDirectory } from "../../hooks/useDirectory";
import { loadLabOrders } from "./labData";
import { asList } from "../../utils/payload";
import { formatDateTime } from "../../utils/formatDate";
import { LAB_STATUSES } from "../../utils/constants";

export default function LabOrders() {
  const { role, userId, isPatient, patientId: ownId, hasRole } = useAuth();
  const dir = useDirectory();
  const scope = usePatientScope();
  const [status, setStatus] = useState("ORDERED");
  const queueRole = !isPatient && role !== "DOCTOR";
  const usesStatus = queueRole && !scope.patientId;

  const state = useApi(
    () => loadLabOrders({ isPatient, ownId, scopePatientId: scope.patientId, role, userId, status }),
    [role, userId, ownId, scope.patientId, status],
    !isPatient || Boolean(ownId),
  );

  if (isPatient && !ownId) return <NoPatientRecord />;

  return (
    <>
      <PageHeader
        eyebrow="Laboratory"
        title="Lab orders"
        actions={hasRole("DOCTOR") && <Link to={`/app/laboratory/orders/new${scope.patientId ? `?patient=${scope.patientId}` : ""}`} className="btn-primary"><Plus size={16} /> Order a test</Link>}
      />
      <PatientScopeBar scope={scope} hint={role === "DOCTOR" ? "Optional — leave empty to list the tests you ordered." : "Optional — leave empty to browse the work queue by status."} />
      <div className="surface p-5">
        {usesStatus && (
          <div className="mb-4">
            <Segmented options={LAB_STATUSES} value={status} onChange={setStatus} />
          </div>
        )}
        <Async state={state}>
          {(data) => (
            <DataTable
              rows={asList(data)}
              rowLink={(o) => `/app/laboratory/orders/${o.id}`}
              empty={<EmptyState title="No lab orders" message="No orders match this view." />}
              columns={[
                { key: "testName", label: "Test" },
                { key: "testCode", label: "Code" },
                ...(isPatient ? [] : [{ key: "patient", label: "Patient", render: (o) => dir.patientName(o.patientId) }]),
                { key: "doctor", label: "Ordered by", render: (o) => dir.doctorName(o.doctorId) },
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

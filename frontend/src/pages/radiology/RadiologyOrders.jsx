import { useState } from "react";
import { Link } from "react-router-dom";
import { Plus } from "lucide-react";
import PageHeader from "../../components/common/PageHeader";
import DataTable from "../../components/common/DataTable";
import EmptyState from "../../components/common/EmptyState";
import Async from "../../components/common/Async";
import Select from "../../components/common/Select";
import StatusBadge from "../../components/common/StatusBadge";
import { PatientScopeBar, usePatientScope } from "../../components/common/Pickers";
import { NoPatientRecord } from "../../components/common/RecordStates";
import { useApi } from "../../hooks/useApi";
import { useAuth } from "../../context/AuthContext";
import { useDirectory } from "../../hooks/useDirectory";
import { radiologyApi } from "../../api/radiologyApi";
import { asList } from "../../utils/payload";
import { formatDateTime } from "../../utils/formatDate";
import { MODALITIES, PRIORITIES, RADIOLOGY_ORDER_STATUSES } from "../../utils/constants";

const FILTERS = {
  status: { label: "Status", values: RADIOLOGY_ORDER_STATUSES, call: radiologyApi.ordersByStatus },
  modality: { label: "Modality", values: MODALITIES, call: radiologyApi.ordersByModality },
  urgency: { label: "Urgency", values: PRIORITIES, call: radiologyApi.ordersByUrgency },
};

export default function RadiologyOrders() {
  const { role, userId, isPatient, patientId: ownId, hasRole } = useAuth();
  const dir = useDirectory();
  const scope = usePatientScope();
  const [kind, setKind] = useState("status");
  const [value, setValue] = useState("ORDERED");
  const isDoctor = role === "DOCTOR";
  const scoped = isPatient ? ownId : scope.patientId;
  const usesFilter = !scoped && !isDoctor;

  const state = useApi(() => {
    if (scoped) return radiologyApi.patientOrders(scoped);
    if (isDoctor) return radiologyApi.doctorOrders(userId);
    return FILTERS[kind].call(value);
  }, [scoped, isDoctor, userId, kind, value], !isPatient || Boolean(ownId));

  if (isPatient && !ownId) return <NoPatientRecord />;

  const changeKind = (next) => {
    setKind(next);
    setValue(FILTERS[next].values[0]);
  };

  return (
    <>
      <PageHeader
        eyebrow="Radiology"
        title="Imaging orders"
        actions={hasRole("DOCTOR") && <Link to={`/app/radiology/orders/new${scope.patientId ? `?patient=${scope.patientId}` : ""}`} className="btn-primary"><Plus size={16} /> Order imaging</Link>}
      />
      <PatientScopeBar scope={scope} hint={isDoctor ? "Optional — leave empty to list the imaging you ordered." : "Optional — leave empty to filter all orders."} />
      <div className="surface p-5">
        {usesFilter && (
          <div className="mb-4 grid gap-4 sm:grid-cols-2 md:max-w-lg">
            <Select label="Filter by" value={kind} onChange={(e) => changeKind(e.target.value)}>
              {Object.entries(FILTERS).map(([k, f]) => <option key={k} value={k}>{f.label}</option>)}
            </Select>
            <Select label={FILTERS[kind].label} value={value} onChange={(e) => setValue(e.target.value)}>
              {FILTERS[kind].values.map((v) => <option key={v} value={v}>{v.replaceAll("_", " ")}</option>)}
            </Select>
          </div>
        )}
        <Async state={state}>
          {(data) => (
            <DataTable
              rows={asList(data)}
              rowLink={(o) => `/app/radiology/orders/${o.id}`}
              empty={<EmptyState title="No imaging orders" message="No orders match this view." />}
              columns={[
                { key: "id", label: "Order", render: (o) => `#${o.id}` },
                { key: "modality", label: "Modality" },
                ...(isPatient ? [] : [{ key: "patient", label: "Patient", render: (o) => o.patientName || dir.patientName(o.patientId) }]),
                { key: "clinicalIndication", label: "Indication", render: (o) => <span className="line-clamp-2">{o.clinicalIndication}</span> },
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

import { useState } from "react";
import { Link } from "react-router-dom";
import { Plus } from "lucide-react";
import PageHeader from "../../components/common/PageHeader";
import DataTable from "../../components/common/DataTable";
import EmptyState from "../../components/common/EmptyState";
import Async from "../../components/common/Async";
import Segmented from "../../components/common/Segmented";
import Select from "../../components/common/Select";
import StatusBadge from "../../components/common/StatusBadge";
import { PatientScopeBar, usePatientScope } from "../../components/common/Pickers";
import { NoPatientRecord } from "../../components/common/RecordStates";
import { useApi } from "../../hooks/useApi";
import { useDirectory } from "../../hooks/useDirectory";
import { useAuth } from "../../context/AuthContext";
import { inpatientApi } from "../../api/inpatientApi";
import { asList } from "../../utils/payload";
import { formatDateTime } from "../../utils/formatDate";
import { ADMISSION_CATEGORIES } from "../../utils/constants";

const VIEWS = [
  { value: "ADMITTED", label: "Admitted" },
  { value: "DISCHARGED", label: "Discharged" },
  { value: "CATEGORY", label: "By category" },
];

export default function Admissions() {
  const { isPatient, patientId: ownId, hasRole } = useAuth();
  const dir = useDirectory();
  const scope = usePatientScope();
  const [view, setView] = useState("ADMITTED");
  const [category, setCategory] = useState(ADMISSION_CATEGORIES[0]);
  const scoped = isPatient ? ownId : scope.patientId;

  const state = useApi(() => {
    if (scoped) return inpatientApi.byPatient(scoped);
    if (view === "CATEGORY") return inpatientApi.byCategory(category);
    return inpatientApi.byStatus(view);
  }, [scoped, view, category], !isPatient || Boolean(ownId));

  if (isPatient && !ownId) return <NoPatientRecord />;

  return (
    <>
      <PageHeader
        eyebrow="Inpatient"
        title={isPatient ? "My admissions" : "Admissions"}
        actions={hasRole("DOCTOR") && <Link to={`/app/inpatient/new${scope.patientId ? `?patient=${scope.patientId}` : ""}`} className="btn-primary"><Plus size={16} /> Admit a patient</Link>}
      />
      <PatientScopeBar scope={scope} hint="Optional — leave empty to browse all admissions by status." />
      <div className="surface p-5">
        {!scoped && (
          <div className="mb-4 flex flex-wrap items-end gap-4">
            <Segmented options={VIEWS} value={view} onChange={setView} />
            {view === "CATEGORY" && (
              <Select label="Category" value={category} onChange={(e) => setCategory(e.target.value)} className="w-52">
                {ADMISSION_CATEGORIES.map((c) => <option key={c}>{c}</option>)}
              </Select>
            )}
          </div>
        )}
        <Async state={state}>
          {(data) => (
            <DataTable
              rows={asList(data)}
              rowLink={(a) => `/app/inpatient/${a.id}`}
              empty={<EmptyState title="No admissions" message="No admissions match this view." />}
              columns={[
                { key: "id", label: "Admission", render: (a) => `#${a.id}` },
                ...(isPatient ? [] : [{ key: "patient", label: "Patient", render: (a) => dir.patientName(a.patientId) }]),
                { key: "bedNumber", label: "Bed" },
                { key: "admissionCategory", label: "Category" },
                { key: "admissionDate", label: "Admitted", render: (a) => formatDateTime(a.admissionDate) },
                { key: "dischargeDate", label: "Discharged", render: (a) => formatDateTime(a.dischargeDate) },
                { key: "status", label: "Status", render: (a) => <StatusBadge status={a.status} /> },
              ]}
            />
          )}
        </Async>
      </div>
    </>
  );
}

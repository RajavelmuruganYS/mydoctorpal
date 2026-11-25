import { Link } from "react-router-dom";
import { Plus } from "lucide-react";
import PageHeader from "../../components/common/PageHeader";
import DataTable from "../../components/common/DataTable";
import EmptyState from "../../components/common/EmptyState";
import Async from "../../components/common/Async";
import StatusBadge from "../../components/common/StatusBadge";
import { PatientScopeBar, usePatientScope } from "../../components/common/Pickers";
import { NoPatientRecord } from "../../components/common/RecordStates";
import { useApi } from "../../hooks/useApi";
import { useDirectory } from "../../hooks/useDirectory";
import { useAuth } from "../../context/AuthContext";
import { prescriptionApi } from "../../api/prescriptionApi";
import { asList } from "../../utils/payload";
import { formatDate } from "../../utils/formatDate";

export default function Prescriptions() {
  const { role, userId, isPatient, patientId: ownId } = useAuth();
  const dir = useDirectory();
  const scope = usePatientScope();
  const isDoctor = role === "DOCTOR";

  // A chosen patient always wins; a doctor otherwise sees their own prescriptions.
  const request = () => {
    if (scope.patientId) return prescriptionApi.byPatient(scope.patientId);
    return prescriptionApi.byDoctor(userId);
  };
  const enabled = isPatient ? Boolean(ownId) : Boolean(scope.patientId) || isDoctor;
  const state = useApi(request, [scope.patientId, userId, isDoctor], enabled);

  if (isPatient && !ownId) return <NoPatientRecord />;

  return (
    <>
      <PageHeader
        eyebrow="Prescriptions"
        title={isPatient ? "My prescriptions" : "Prescriptions"}
        description={isDoctor && !scope.patientId ? "Prescriptions you have written. Choose a patient to see all of theirs." : undefined}
        actions={
          isDoctor && (
            <Link to={`/app/prescriptions/new${scope.patientId ? `?patient=${scope.patientId}` : ""}`} className="btn-primary">
              <Plus size={16} /> New prescription
            </Link>
          )
        }
      />
      <PatientScopeBar scope={scope} hint={isDoctor ? "Optional — leave empty to list your own prescriptions." : "Choose a patient to view their prescriptions."} />
      {!enabled ? (
        <EmptyState title="Choose a patient" message="Select a patient above to list their prescriptions." />
      ) : (
        <div className="surface p-5">
          <Async state={state}>
            {(data) => (
              <DataTable
                rows={asList(data).sort((a, b) => String(b.prescriptionDate).localeCompare(String(a.prescriptionDate)))}
                rowLink={(p) => `/app/prescriptions/${p.id}`}
                empty={<EmptyState title="No prescriptions" message="No prescriptions were found." />}
                columns={[
                  { key: "id", label: "Prescription", render: (p) => `#${p.id}` },
                  { key: "prescriptionDate", label: "Date", render: (p) => formatDate(p.prescriptionDate) },
                  ...(isPatient ? [] : [{ key: "patient", label: "Patient", render: (p) => dir.patientName(p.patientId) }]),
                  { key: "doctor", label: "Doctor", render: (p) => dir.doctorName(p.doctorId) },
                  { key: "diagnosisCode", label: "Diagnosis" },
                  { key: "status", label: "Status", render: (p) => <StatusBadge status={p.status} /> },
                  { key: "dispenseStatus", label: "Dispensing", render: (p) => <StatusBadge status={p.dispenseStatus} /> },
                ]}
              />
            )}
          </Async>
        </div>
      )}
    </>
  );
}

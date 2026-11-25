import { useState } from "react";
import { useParams } from "react-router-dom";
import PageHeader from "../../components/common/PageHeader";
import Async from "../../components/common/Async";
import DetailGrid from "../../components/common/DetailGrid";
import StatusBadge from "../../components/common/StatusBadge";
import Button from "../../components/common/Button";
import ConfirmDialog from "../../components/common/ConfirmDialog";
import { BackLink } from "../../components/common/RecordStates";
import { useApi } from "../../hooks/useApi";
import { useMutation } from "../../hooks/useMutation";
import { useDirectory } from "../../hooks/useDirectory";
import { useAuth } from "../../context/AuthContext";
import { inpatientApi } from "../../api/inpatientApi";
import { formatDateTime } from "../../utils/formatDate";

export default function AdmissionDetails() {
  const { id } = useParams();
  const { hasRole, isPatient } = useAuth();
  const dir = useDirectory();
  const [confirm, setConfirm] = useState(false);
  const state = useApi(() => inpatientApi.get(id), [id]);
  const discharge = useMutation(() => inpatientApi.discharge(id, {}), { success: "Patient discharged." });

  const doDischarge = async () => {
    const res = await discharge.run();
    setConfirm(false);
    if (res) state.reload();
  };

  return (
    <>
      <BackLink to="/app/inpatient">Admissions</BackLink>
      <Async state={state}>
        {(a) => (
          <>
            <PageHeader
              eyebrow="Admission"
              title={`Admission #${a.id}`}
              actions={
                <>
                  <StatusBadge status={a.status} />
                  {a.status === "ADMITTED" && hasRole("DOCTOR", "NURSE", "ADMIN") && (
                    <Button onClick={() => setConfirm(true)}>Discharge patient</Button>
                  )}
                </>
              }
            />
            <div className="surface p-6">
              <DetailGrid
                items={[
                  { label: "Patient", value: isPatient ? "You" : dir.patientName(a.patientId) },
                  { label: "Admitting doctor", value: dir.doctorName(a.admittingDoctorId) },
                  { label: "Bed", value: a.bedNumber },
                  { label: "Category", value: a.admissionCategory },
                  { label: "Admitted", value: formatDateTime(a.admissionDate) },
                  { label: "Discharged", value: formatDateTime(a.dischargeDate) },
                  { label: "Diagnosis on admission", value: a.diagnosisOnAdmission },
                ]}
              />
            </div>
            <ConfirmDialog
              open={confirm}
              loading={discharge.loading}
              title="Discharge this patient?"
              message="The admission will be closed and the discharge time recorded."
              confirmLabel="Discharge"
              onClose={() => setConfirm(false)}
              onConfirm={doDischarge}
            />
          </>
        )}
      </Async>
    </>
  );
}

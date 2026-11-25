import { useState } from "react";
import { Link, useParams } from "react-router-dom";
import PageHeader from "../../components/common/PageHeader";
import Async from "../../components/common/Async";
import DetailGrid from "../../components/common/DetailGrid";
import DataTable from "../../components/common/DataTable";
import StatusBadge from "../../components/common/StatusBadge";
import ConfirmDialog from "../../components/common/ConfirmDialog";
import Button from "../../components/common/Button";
import EmptyState from "../../components/common/EmptyState";
import { BackLink } from "../../components/common/RecordStates";
import { useApi } from "../../hooks/useApi";
import { useMutation } from "../../hooks/useMutation";
import { useDirectory } from "../../hooks/useDirectory";
import { useAuth } from "../../context/AuthContext";
import { prescriptionApi } from "../../api/prescriptionApi";
import { pharmacyApi } from "../../api/pharmacyApi";
import { asList } from "../../utils/payload";
import { formatDate, formatDateTime } from "../../utils/formatDate";

export default function PrescriptionDetails() {
  const { id } = useParams();
  const { hasRole, userId, isPatient } = useAuth();
  const dir = useDirectory();
  const [signOpen, setSignOpen] = useState(false);
  const state = useApi(() => prescriptionApi.get(id), [id]);
  const items = useApi(() => prescriptionApi.items(id), [id]);
  const canSeeDispensations = hasRole("ADMIN", "PHARMACIST", "DOCTOR");
  const dispensations = useApi(() => pharmacyApi.byPrescription(id), [id], canSeeDispensations);
  const sign = useMutation(() => prescriptionApi.sign(id), { success: "Prescription signed." });

  const doSign = async () => {
    if (await sign.run()) {
      setSignOpen(false);
      state.reload();
    }
  };

  return (
    <>
      <BackLink to="/app/prescriptions">Prescriptions</BackLink>
      <Async state={state}>
        {(rx) => (
          <>
            <PageHeader
              eyebrow="Prescription"
              title={`Prescription #${rx.id}`}
              description={`Written ${formatDate(rx.prescriptionDate)} · diagnosis ${rx.diagnosisCode}`}
              actions={
                <>
                  <StatusBadge status={rx.status} />
                  <StatusBadge status={rx.dispenseStatus} />
                  {rx.status === "DRAFT" && hasRole("DOCTOR") && rx.doctorId === userId && (
                    <Button onClick={() => setSignOpen(true)}>Sign prescription</Button>
                  )}
                  {rx.status === "SIGNED" && rx.dispenseStatus !== "DISPENSED" && hasRole("PHARMACIST", "ADMIN") && (
                    <Link to={`/app/pharmacy/dispense/${rx.id}`} className="btn-primary">Dispense</Link>
                  )}
                </>
              }
            />
            <div className="surface p-6">
              <DetailGrid
                items={[
                  { label: "Patient", value: isPatient ? "You" : dir.patientName(rx.patientId) },
                  { label: "Doctor", value: dir.doctorName(rx.doctorId) },
                  { label: "Diagnosis code", value: rx.diagnosisCode },
                  { label: "Prescription date", value: formatDate(rx.prescriptionDate) },
                  { label: "Expires", value: formatDateTime(rx.expiresAt) },
                  { label: "Signature", value: rx.doctorSignatureHash ? "Signed digitally" : "Not signed" },
                ]}
              />
            </div>
            <div className="surface mt-6 p-6">
              <h3 className="mb-4 font-bold">Medications</h3>
              <Async state={items} inline>
                {(data) => (
                  <DataTable
                    rows={asList(data)}
                    empty={<EmptyState title="No medications" />}
                    columns={[
                      { key: "brandName", label: "Drug", render: (i) => `${i.brandName} (${i.genericName})` },
                      { key: "dosageForm", label: "Form" },
                      { key: "strength", label: "Strength" },
                      { key: "frequency", label: "Frequency" },
                      { key: "durationDays", label: "Days" },
                      { key: "specialInstructions", label: "Instructions" },
                    ]}
                  />
                )}
              </Async>
            </div>
            {canSeeDispensations && (
              <div className="surface mt-6 p-6">
                <h3 className="mb-4 font-bold">Dispensing history</h3>
                <Async state={dispensations} inline>
                  {(data) => (
                    <DataTable
                      rows={asList(data)}
                      rowLink={(d) => `/app/pharmacy/dispensations/${d.id}`}
                      empty={<EmptyState title="Not dispensed yet" message="No dispensing has been recorded for this prescription." />}
                      columns={[
                        { key: "id", label: "Dispensation", render: (d) => `#${d.id}` },
                        { key: "dispensedAt", label: "Dispensed", render: (d) => formatDateTime(d.dispensedAt) },
                        { key: "status", label: "Status", render: (d) => <StatusBadge status={d.status} /> },
                        { key: "notes", label: "Notes" },
                      ]}
                    />
                  )}
                </Async>
              </div>
            )}
            <ConfirmDialog
              open={signOpen}
              loading={sign.loading}
              title="Sign this prescription?"
              message="Once signed the prescription is locked and the pharmacy can dispense it."
              confirmLabel="Sign"
              onClose={() => setSignOpen(false)}
              onConfirm={doSign}
            />
          </>
        )}
      </Async>
    </>
  );
}

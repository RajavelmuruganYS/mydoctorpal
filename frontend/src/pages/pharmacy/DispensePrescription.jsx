import { useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import PageHeader from "../../components/common/PageHeader";
import Async from "../../components/common/Async";
import DetailGrid from "../../components/common/DetailGrid";
import DataTable from "../../components/common/DataTable";
import StatusBadge from "../../components/common/StatusBadge";
import Textarea from "../../components/common/Textarea";
import Button from "../../components/common/Button";
import ConfirmDialog from "../../components/common/ConfirmDialog";
import { BackLink } from "../../components/common/RecordStates";
import { useApi } from "../../hooks/useApi";
import { useMutation } from "../../hooks/useMutation";
import { prescriptionApi } from "../../api/prescriptionApi";
import { pharmacyApi } from "../../api/pharmacyApi";
import { patientApi } from "../../api/patientApi";
import { asList, personName } from "../../utils/payload";
import { formatDate } from "../../utils/formatDate";

export default function DispensePrescription() {
  const { prescriptionId } = useParams();
  const navigate = useNavigate();
  const [notes, setNotes] = useState("");
  const [confirm, setConfirm] = useState(false);
  const rx = useApi(() => prescriptionApi.get(prescriptionId), [prescriptionId]);
  const items = useApi(() => prescriptionApi.items(prescriptionId), [prescriptionId]);
  const patient = useApi(() => patientApi.get(rx.data.patientId), [rx.data?.patientId], Boolean(rx.data?.patientId));
  const dispense = useMutation((body) => pharmacyApi.dispense(prescriptionId, body), { success: "Prescription dispensed." });

  const submit = async () => {
    const res = await dispense.run(notes.trim() ? { notes: notes.trim() } : {});
    setConfirm(false);
    if (res) navigate(res.data?.id ? `/app/pharmacy/dispensations/${res.data.id}` : "/app/pharmacy");
  };

  return (
    <>
      <BackLink to="/app/pharmacy">Pharmacy</BackLink>
      <Async state={rx}>
        {(p) => {
          const ready = p.status === "SIGNED" && p.dispenseStatus !== "DISPENSED";
          return (
            <>
              <PageHeader eyebrow="Dispense" title={`Prescription #${p.id}`} actions={<><StatusBadge status={p.status} /><StatusBadge status={p.dispenseStatus} /></>} />
              {!ready && (
                <div className="mb-5 rounded-xl bg-amber-50 p-4 text-sm text-amber-900">
                  {p.dispenseStatus === "DISPENSED"
                    ? "This prescription has already been dispensed."
                    : "Only signed prescriptions can be dispensed. Ask the prescribing doctor to sign it first."}
                </div>
              )}
              <div className="surface p-6">
                <DetailGrid
                  items={[
                    { label: "Patient", value: patient.data ? `${personName(patient.data)} (${patient.data.patientNumber})` : `Patient #${p.patientId}` },
                    { label: "Allergies", value: patient.data?.allergies || "None recorded" },
                    { label: "Diagnosis code", value: p.diagnosisCode },
                    { label: "Prescription date", value: formatDate(p.prescriptionDate) },
                  ]}
                />
              </div>
              <div className="surface mt-6 p-6">
                <h3 className="mb-4 font-bold">Medications to dispense</h3>
                <Async state={items} inline>
                  {(data) => (
                    <DataTable
                      rows={asList(data)}
                      columns={[
                        { key: "brandName", label: "Drug", render: (i) => `${i.brandName} (${i.genericName})` },
                        { key: "strength", label: "Strength" },
                        { key: "dosageForm", label: "Form" },
                        { key: "frequency", label: "Frequency" },
                        { key: "durationDays", label: "Days" },
                        { key: "specialInstructions", label: "Instructions" },
                      ]}
                    />
                  )}
                </Async>
              </div>
              {ready && (
                <div className="surface mt-6 space-y-4 p-6">
                  <Textarea label="Dispensing notes (optional)" maxLength={2000} value={notes} onChange={(e) => setNotes(e.target.value)} />
                  <div className="flex justify-end gap-3">
                    <Link to="/app/pharmacy" className="btn-secondary">Cancel</Link>
                    <Button onClick={() => setConfirm(true)}>Dispense prescription</Button>
                  </div>
                </div>
              )}
              <ConfirmDialog
                open={confirm}
                loading={dispense.loading}
                title="Dispense this prescription?"
                message="This records the dispensation and marks the prescription as dispensed. It cannot be undone."
                confirmLabel="Dispense"
                onClose={() => setConfirm(false)}
                onConfirm={submit}
              />
            </>
          );
        }}
      </Async>
    </>
  );
}

import { useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { Plus, Trash2 } from "lucide-react";
import PageHeader from "../../components/common/PageHeader";
import Input from "../../components/common/Input";
import Button from "../../components/common/Button";
import FormActions from "../../components/common/FormActions";
import { DrugSelect, PatientSelect } from "../../components/common/Pickers";
import { BackLink } from "../../components/common/RecordStates";
import { useMutation } from "../../hooks/useMutation";
import { prescriptionApi } from "../../api/prescriptionApi";
import { toInt } from "../../utils/payload";

const emptyItem = () => ({ key: Math.random(), drugId: "", dosageForm: "", strength: "", frequency: "", durationDays: "", specialInstructions: "" });
const FREQUENCIES = ["Once daily", "Twice daily", "Three times daily", "Every 8 hours", "At bedtime", "As needed"];

export default function CreatePrescription() {
  const navigate = useNavigate();
  const [params] = useSearchParams();
  const [patientId, setPatientId] = useState(params.get("patient") ? Number(params.get("patient")) : "");
  const [diagnosisCode, setDiagnosisCode] = useState("");
  const [items, setItems] = useState([emptyItem()]);
  const [error, setError] = useState("");
  const save = useMutation(prescriptionApi.create, { success: "Prescription created as a draft. Sign it to release it." });

  const patch = (key, changes) => setItems((list) => list.map((i) => (i.key === key ? { ...i, ...changes } : i)));

  const submit = async (event) => {
    event.preventDefault();
    setError("");
    if (!patientId) return setError("Select a patient.");
    if (items.some((i) => !i.drugId)) return setError("Choose a drug for every line.");
    const body = {
      patientId,
      diagnosisCode: diagnosisCode.trim(),
      items: items.map((i) => ({
        drugId: i.drugId,
        dosageForm: i.dosageForm.trim(),
        strength: i.strength.trim(),
        frequency: i.frequency.trim(),
        durationDays: toInt(i.durationDays),
        ...(i.specialInstructions.trim() ? { specialInstructions: i.specialInstructions.trim() } : {}),
      })),
    };
    const res = await save.run(body);
    if (res) navigate(res.data?.id ? `/app/prescriptions/${res.data.id}` : "/app/prescriptions");
  };

  return (
    <>
      <BackLink to="/app/prescriptions">Prescriptions</BackLink>
      <PageHeader eyebrow="Prescriptions" title="New prescription" description="Saved as a draft. A prescription can only be dispensed after you sign it." />
      <form onSubmit={submit} className="space-y-6">
        <div className="surface grid gap-4 p-6 md:grid-cols-2">
          <PatientSelect required value={patientId} onChange={(id) => setPatientId(id)} />
          <Input label="Diagnosis code" required maxLength={20} placeholder="e.g. J45.9" value={diagnosisCode} onChange={(e) => setDiagnosisCode(e.target.value)} error={save.fieldErrors.diagnosisCode} />
        </div>
        {items.map((item, index) => (
          <div key={item.key} className="surface space-y-4 p-6">
            <div className="flex items-center justify-between">
              <h3 className="font-bold">Medication {index + 1}</h3>
              {items.length > 1 && (
                <button type="button" onClick={() => setItems((l) => l.filter((i) => i.key !== item.key))} className="flex items-center gap-1 text-sm font-bold text-rose-600">
                  <Trash2 size={15} /> Remove
                </button>
              )}
            </div>
            <DrugSelect
              required
              value={item.drugId}
              onChange={(id, drug) => patch(item.key, { drugId: id, dosageForm: drug?.dosageForm || "", strength: drug?.strength || "" })}
            />
            <div className="grid gap-4 md:grid-cols-4">
              <Input label="Dosage form" required maxLength={100} value={item.dosageForm} onChange={(e) => patch(item.key, { dosageForm: e.target.value })} />
              <Input label="Strength" required maxLength={100} value={item.strength} onChange={(e) => patch(item.key, { strength: e.target.value })} />
              <Input label="Frequency" required maxLength={100} list="frequencies" value={item.frequency} onChange={(e) => patch(item.key, { frequency: e.target.value })} />
              <Input label="Duration (days)" type="number" required min={1} value={item.durationDays} onChange={(e) => patch(item.key, { durationDays: e.target.value })} />
            </div>
            <Input label="Special instructions" maxLength={2000} value={item.specialInstructions} onChange={(e) => patch(item.key, { specialInstructions: e.target.value })} />
          </div>
        ))}
        <datalist id="frequencies">{FREQUENCIES.map((f) => <option key={f} value={f} />)}</datalist>
        <Button variant="secondary" onClick={() => setItems((l) => [...l, emptyItem()])}>
          <Plus size={15} /> Add medication
        </Button>
        {error && <div className="rounded-xl bg-rose-50 p-3 text-sm text-rose-700" role="alert">{error}</div>}
        <FormActions loading={save.loading} cancelTo="/app/prescriptions" submitLabel="Save draft" />
      </form>
    </>
  );
}

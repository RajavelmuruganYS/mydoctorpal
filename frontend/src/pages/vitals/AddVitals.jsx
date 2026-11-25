import { useNavigate, useSearchParams } from "react-router-dom";
import PageHeader from "../../components/common/PageHeader";
import Input from "../../components/common/Input";
import FormActions from "../../components/common/FormActions";
import { PatientSelect } from "../../components/common/Pickers";
import { BackLink } from "../../components/common/RecordStates";
import { useForm } from "../../hooks/useForm";
import { useMutation } from "../../hooks/useMutation";
import { vitalsApi } from "../../api/vitalsApi";
import { compact, toInt, toNumber } from "../../utils/payload";

export default function AddVitals() {
  const navigate = useNavigate();
  const [params] = useSearchParams();
  const form = useForm({ patientId: params.get("patient") ? Number(params.get("patient")) : "", temperature: "", bpSystolic: "", bpDiastolic: "", pulseRate: "", spo2: "", respiratoryRate: "" });
  const save = useMutation(vitalsApi.create, { success: "Vitals recorded." });

  const submit = async (event) => {
    event.preventDefault();
    const v = form.values;
    if (!v.patientId) return form.setErrors({ patientId: "Select a patient." });
    const body = compact({
      patientId: v.patientId,
      temperature: toNumber(v.temperature),
      bpSystolic: toInt(v.bpSystolic),
      bpDiastolic: toInt(v.bpDiastolic),
      pulseRate: toInt(v.pulseRate),
      spo2: toNumber(v.spo2),
      respiratoryRate: toInt(v.respiratoryRate),
    });
    if (Object.keys(body).length === 1) return form.setErrors({ temperature: "Enter at least one measurement." });
    if (await save.run(body)) navigate(`/app/vitals?patient=${v.patientId}`);
  };

  const num = (name, label, min, max, step = 1, unit) => (
    <Input label={label} type="number" min={min} max={max} step={step} hint={`${min}–${max}${unit ? ` ${unit}` : ""}`} {...form.bind(name, save.fieldErrors)} />
  );

  return (
    <>
      <BackLink to="/app/vitals">Vitals</BackLink>
      <PageHeader eyebrow="Vitals" title="Record vital signs" description="Fill in the measurements you took; leave the rest empty." />
      <form onSubmit={submit} className="surface space-y-5 p-6">
        <PatientSelect required value={form.values.patientId} onChange={(id) => form.set("patientId", id)} error={form.errors.patientId || save.fieldErrors.patientId} />
        <div className="grid gap-4 md:grid-cols-3">
          {num("temperature", "Temperature (°C)", 20, 50, 0.1)}
          {num("bpSystolic", "BP systolic (mmHg)", 40, 250)}
          {num("bpDiastolic", "BP diastolic (mmHg)", 20, 150)}
          {num("pulseRate", "Pulse (bpm)", 30, 250)}
          {num("spo2", "SpO₂ (%)", 50, 100, 0.1)}
          {num("respiratoryRate", "Respiratory rate (/min)", 5, 80)}
        </div>
        <FormActions loading={save.loading} cancelTo="/app/vitals" submitLabel="Save vitals" />
      </form>
    </>
  );
}

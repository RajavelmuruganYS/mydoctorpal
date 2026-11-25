import { useNavigate, useSearchParams } from "react-router-dom";
import PageHeader from "../../components/common/PageHeader";
import Select from "../../components/common/Select";
import Textarea from "../../components/common/Textarea";
import FormActions from "../../components/common/FormActions";
import { PatientSelect } from "../../components/common/Pickers";
import { BackLink } from "../../components/common/RecordStates";
import { useForm } from "../../hooks/useForm";
import { useMutation } from "../../hooks/useMutation";
import { radiologyApi } from "../../api/radiologyApi";
import { compact } from "../../utils/payload";
import { MODALITIES, PRIORITIES } from "../../utils/constants";

export default function CreateRadiologyOrder() {
  const navigate = useNavigate();
  const [params] = useSearchParams();
  const form = useForm({ patientId: params.get("patient") ? Number(params.get("patient")) : "", modality: "XRAY", urgency: "ROUTINE", clinicalIndication: "", clinicalHistory: "" });
  const save = useMutation(radiologyApi.createOrder, { success: "Imaging order placed." });

  const submit = async (event) => {
    event.preventDefault();
    if (!form.values.patientId) return form.setErrors({ patientId: "Select a patient." });
    const res = await save.run(compact(form.values));
    if (res) navigate(res.data?.id ? `/app/radiology/orders/${res.data.id}` : "/app/radiology/orders");
  };

  return (
    <>
      <BackLink to="/app/radiology">Radiology</BackLink>
      <PageHeader eyebrow="Radiology" title="Order imaging" />
      <form onSubmit={submit} className="surface space-y-5 p-6">
        <PatientSelect required value={form.values.patientId} onChange={(id) => form.set("patientId", id)} error={form.errors.patientId || save.fieldErrors.patientId} />
        <div className="grid gap-4 md:grid-cols-2">
          <Select label="Modality" required {...form.bind("modality", save.fieldErrors)}>
            {MODALITIES.map((m) => <option key={m}>{m}</option>)}
          </Select>
          <Select label="Urgency" {...form.bind("urgency", save.fieldErrors)}>
            {PRIORITIES.map((p) => <option key={p}>{p}</option>)}
          </Select>
        </div>
        <Textarea label="Clinical indication" required {...form.bind("clinicalIndication", save.fieldErrors)} />
        <Textarea label="Clinical history" {...form.bind("clinicalHistory", save.fieldErrors)} />
        <FormActions loading={save.loading} cancelTo="/app/radiology" submitLabel="Place order" />
      </form>
    </>
  );
}

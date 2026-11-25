import { useNavigate, useSearchParams } from "react-router-dom";
import PageHeader from "../../components/common/PageHeader";
import Input from "../../components/common/Input";
import Textarea from "../../components/common/Textarea";
import FormActions from "../../components/common/FormActions";
import { PatientSelect } from "../../components/common/Pickers";
import { BackLink } from "../../components/common/RecordStates";
import { useForm } from "../../hooks/useForm";
import { useMutation } from "../../hooks/useMutation";
import { inpatientApi } from "../../api/inpatientApi";
import { compact } from "../../utils/payload";
import { ADMISSION_CATEGORIES } from "../../utils/constants";

export default function CreateAdmission() {
  const navigate = useNavigate();
  const [params] = useSearchParams();
  const form = useForm({ patientId: params.get("patient") ? Number(params.get("patient")) : "", bedNumber: "", admissionCategory: "GENERAL", diagnosisOnAdmission: "" });
  const save = useMutation(inpatientApi.create, { success: "Patient admitted." });

  const submit = async (event) => {
    event.preventDefault();
    if (!form.values.patientId) return form.setErrors({ patientId: "Select a patient." });
    const res = await save.run(compact(form.values));
    if (res) navigate(res.data?.id ? `/app/inpatient/${res.data.id}` : "/app/inpatient");
  };

  return (
    <>
      <BackLink to="/app/inpatient">Admissions</BackLink>
      <PageHeader eyebrow="Inpatient" title="Admit a patient" description="A patient can only have one active admission at a time." />
      <form onSubmit={submit} className="surface space-y-5 p-6">
        <PatientSelect required value={form.values.patientId} onChange={(id) => form.set("patientId", id)} error={form.errors.patientId || save.fieldErrors.patientId} />
        <div className="grid gap-4 md:grid-cols-2">
          <Input label="Bed number" maxLength={50} {...form.bind("bedNumber", save.fieldErrors)} />
          <Input label="Category" maxLength={50} list="admission-categories" {...form.bind("admissionCategory", save.fieldErrors)} />
        </div>
        <datalist id="admission-categories">{ADMISSION_CATEGORIES.map((c) => <option key={c} value={c} />)}</datalist>
        <Textarea label="Diagnosis on admission" {...form.bind("diagnosisOnAdmission", save.fieldErrors)} />
        <FormActions loading={save.loading} cancelTo="/app/inpatient" submitLabel="Admit patient" />
      </form>
    </>
  );
}

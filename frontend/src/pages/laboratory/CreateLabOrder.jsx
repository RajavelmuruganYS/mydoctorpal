import { useNavigate, useSearchParams } from "react-router-dom";
import PageHeader from "../../components/common/PageHeader";
import Input from "../../components/common/Input";
import Select from "../../components/common/Select";
import Textarea from "../../components/common/Textarea";
import FormActions from "../../components/common/FormActions";
import { PatientSelect } from "../../components/common/Pickers";
import { BackLink } from "../../components/common/RecordStates";
import { useForm } from "../../hooks/useForm";
import { useMutation } from "../../hooks/useMutation";
import { laboratoryApi } from "../../api/laboratoryApi";
import { compact } from "../../utils/payload";
import { PRIORITIES } from "../../utils/constants";

export default function CreateLabOrder() {
  const navigate = useNavigate();
  const [params] = useSearchParams();
  const form = useForm({ patientId: params.get("patient") ? Number(params.get("patient")) : "", testName: "", testCode: "", priority: "ROUTINE", clinicalNotes: "" });
  const save = useMutation(laboratoryApi.createOrder, { success: "Lab order placed." });

  const submit = async (event) => {
    event.preventDefault();
    if (!form.values.patientId) return form.setErrors({ patientId: "Select a patient." });
    const res = await save.run(compact(form.values));
    if (res) navigate(res.data?.id ? `/app/laboratory/orders/${res.data.id}` : "/app/laboratory/orders");
  };

  return (
    <>
      <BackLink to="/app/laboratory">Laboratory</BackLink>
      <PageHeader eyebrow="Laboratory" title="Order a lab test" />
      <form onSubmit={submit} className="surface space-y-5 p-6">
        <PatientSelect required value={form.values.patientId} onChange={(id) => form.set("patientId", id)} error={form.errors.patientId || save.fieldErrors.patientId} />
        <div className="grid gap-4 md:grid-cols-3">
          <Input label="Test name" required maxLength={200} placeholder="Complete blood count" {...form.bind("testName", save.fieldErrors)} />
          <Input label="Test code" maxLength={50} placeholder="CBC" {...form.bind("testCode", save.fieldErrors)} />
          <Select label="Priority" {...form.bind("priority", save.fieldErrors)}>
            {PRIORITIES.map((p) => <option key={p}>{p}</option>)}
          </Select>
        </div>
        <Textarea label="Clinical notes" maxLength={5000} {...form.bind("clinicalNotes", save.fieldErrors)} />
        <FormActions loading={save.loading} cancelTo="/app/laboratory" submitLabel="Place order" />
      </form>
    </>
  );
}

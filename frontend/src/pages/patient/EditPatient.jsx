import { useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import PageHeader from "../../components/common/PageHeader";
import Async from "../../components/common/Async";
import Input from "../../components/common/Input";
import Select from "../../components/common/Select";
import Textarea from "../../components/common/Textarea";
import Button from "../../components/common/Button";
import FormActions from "../../components/common/FormActions";
import { BackLink } from "../../components/common/RecordStates";
import { useApi } from "../../hooks/useApi";
import { useForm } from "../../hooks/useForm";
import { useMutation } from "../../hooks/useMutation";
import { patientApi } from "../../api/patientApi";
import { compact, personName } from "../../utils/payload";
import { BLOOD_GROUPS, GENDERS } from "../../utils/constants";
import { todayISO } from "../../utils/formatDate";

const FIELDS = ["dateOfBirth", "gender", "bloodGroup", "address", "emergencyContactName", "emergencyContactPhone", "medicalHistory", "allergies"];

function EditForm({ patient, onReload }) {
  const navigate = useNavigate();
  const form = useForm(Object.fromEntries(FIELDS.map((f) => [f, patient[f] ?? ""])));
  const save = useMutation((body) => patientApi.update(patient.id, body), { success: "Patient updated." });
  const conflict = save.status === 409;

  // A reload brings a new version; reset the form to the latest data.
  useEffect(() => {
    form.setValues(Object.fromEntries(FIELDS.map((f) => [f, patient[f] ?? ""])));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [patient.version]);

  const submit = async (event) => {
    event.preventDefault();
    const body = compact({ ...form.values, version: patient.version });
    if (await save.run(body)) navigate(`/app/patients/${patient.id}`);
  };

  return (
    <form onSubmit={submit} className="surface space-y-5 p-6">
      {conflict && (
        <div className="flex items-center justify-between rounded-xl bg-amber-50 p-4 text-sm text-amber-900">
          Someone else changed this record while you were editing.
          <Button variant="secondary" onClick={onReload}>Load latest</Button>
        </div>
      )}
      <div className="grid gap-4 md:grid-cols-3">
        <Input label="Date of birth" type="date" required max={todayISO()} {...form.bind("dateOfBirth", save.fieldErrors)} />
        <Select label="Gender" required {...form.bind("gender", save.fieldErrors)}>
          <option value="">Select…</option>
          {GENDERS.map((g) => <option key={g}>{g}</option>)}
        </Select>
        <Select label="Blood group" {...form.bind("bloodGroup", save.fieldErrors)}>
          <option value="">Unknown</option>
          {BLOOD_GROUPS.map((g) => <option key={g}>{g}</option>)}
        </Select>
      </div>
      <Input label="Address" {...form.bind("address", save.fieldErrors)} />
      <div className="grid gap-4 md:grid-cols-2">
        <Input label="Emergency contact name" {...form.bind("emergencyContactName", save.fieldErrors)} />
        <Input label="Emergency contact phone" {...form.bind("emergencyContactPhone", save.fieldErrors)} />
      </div>
      <Textarea label="Allergies" {...form.bind("allergies", save.fieldErrors)} />
      <Textarea label="Medical history" {...form.bind("medicalHistory", save.fieldErrors)} />
      <FormActions loading={save.loading} cancelTo={`/app/patients/${patient.id}`} submitLabel="Save changes" />
    </form>
  );
}

export default function EditPatient() {
  const { id } = useParams();
  const state = useApi(() => patientApi.get(id), [id]);
  return (
    <>
      <BackLink to={`/app/patients/${id}`}>Back to patient</BackLink>
      <Async state={state}>
        {(patient) => (
          <>
            <PageHeader eyebrow="Edit patient" title={personName(patient) || patient.patientNumber} description="Name and email belong to the user account and are not edited here." />
            <EditForm patient={patient} onReload={state.reload} />
          </>
        )}
      </Async>
    </>
  );
}

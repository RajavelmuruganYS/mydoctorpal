import { useMemo } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import PageHeader from "../../components/common/PageHeader";
import Input from "../../components/common/Input";
import Select from "../../components/common/Select";
import Textarea from "../../components/common/Textarea";
import FormActions from "../../components/common/FormActions";
import { BackLink } from "../../components/common/RecordStates";
import { useApi } from "../../hooks/useApi";
import { useForm } from "../../hooks/useForm";
import { useMutation } from "../../hooks/useMutation";
import { adminApi } from "../../api/adminApi";
import { patientApi } from "../../api/patientApi";
import { asList, compact, personName } from "../../utils/payload";
import { BLOOD_GROUPS, GENDERS } from "../../utils/constants";
import { todayISO } from "../../utils/formatDate";

export default function CreatePatient() {
  const navigate = useNavigate();
  const [params] = useSearchParams();
  const users = useApi(() => adminApi.byRole("PATIENT"), []);
  const patients = useApi(() => patientApi.list(), []);
  const form = useForm({
    userId: params.get("userId") || "",
    patientNumber: "",
    dateOfBirth: "",
    gender: "",
    bloodGroup: "",
    address: "",
    emergencyContactName: "",
    emergencyContactPhone: "",
    medicalHistory: "",
    allergies: "",
  });
  const save = useMutation(patientApi.create, { success: "Patient record created." });

  // Accounts with the PATIENT role that do not have a patient record yet.
  const available = useMemo(() => {
    const taken = new Set(asList(patients.data).map((p) => p.userId));
    return asList(users.data).filter((u) => !taken.has(u.userId));
  }, [users.data, patients.data]);

  const suggested = useMemo(() => {
    const year = new Date().getFullYear();
    const max = asList(patients.data).reduce((m, p) => {
      const n = parseInt(String(p.patientNumber).split("-").pop(), 10);
      return Number.isNaN(n) ? m : Math.max(m, n);
    }, 0);
    return `PAT-${year}-${String(max + 1).padStart(6, "0")}`;
  }, [patients.data]);

  const submit = async (event) => {
    event.preventDefault();
    const body = compact({
      ...form.values,
      userId: Number(form.values.userId),
      patientNumber: (form.values.patientNumber || suggested).trim(),
    });
    const res = await save.run(body);
    if (res) navigate(`/app/patients/${res.data?.id ?? ""}`.replace(/\/$/, ""));
  };

  return (
    <>
      <BackLink to="/app/patients">All patients</BackLink>
      <PageHeader eyebrow="Patients" title="Create patient record" description="Links a registered patient account to a clinical record. Registered patients cannot see clinical data until this exists." />
      <form onSubmit={submit} className="surface space-y-5 p-6">
        <Select label="Patient account" required hint="Only PATIENT accounts without a record are listed." {...form.bind("userId", save.fieldErrors)}>
          <option value="">{users.loading ? "Loading…" : "Select an account…"}</option>
          {available.map((u) => (
            <option key={u.userId} value={u.userId}>
              {personName(u)} — {u.email}
            </option>
          ))}
        </Select>
        <div className="grid gap-4 md:grid-cols-3">
          <Input label="Patient number" placeholder={suggested} hint="Leave empty to use the suggested number." {...form.bind("patientNumber", save.fieldErrors)} />
          <Input label="Date of birth" type="date" required max={todayISO()} {...form.bind("dateOfBirth", save.fieldErrors)} />
          <Select label="Gender" required {...form.bind("gender", save.fieldErrors)}>
            <option value="">Select…</option>
            {GENDERS.map((g) => <option key={g}>{g}</option>)}
          </Select>
        </div>
        <div className="grid gap-4 md:grid-cols-3">
          <Select label="Blood group" {...form.bind("bloodGroup", save.fieldErrors)}>
            <option value="">Unknown</option>
            {BLOOD_GROUPS.map((g) => <option key={g}>{g}</option>)}
          </Select>
          <Input label="Emergency contact name" {...form.bind("emergencyContactName", save.fieldErrors)} />
          <Input label="Emergency contact phone" {...form.bind("emergencyContactPhone", save.fieldErrors)} />
        </div>
        <Input label="Address" {...form.bind("address", save.fieldErrors)} />
        <Textarea label="Allergies" {...form.bind("allergies", save.fieldErrors)} />
        <Textarea label="Medical history" {...form.bind("medicalHistory", save.fieldErrors)} />
        <FormActions loading={save.loading} cancelTo="/app/patients" submitLabel="Create record" />
      </form>
    </>
  );
}

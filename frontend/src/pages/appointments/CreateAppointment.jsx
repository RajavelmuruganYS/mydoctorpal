import { useNavigate } from "react-router-dom";
import PageHeader from "../../components/common/PageHeader";
import Input from "../../components/common/Input";
import Textarea from "../../components/common/Textarea";
import FormActions from "../../components/common/FormActions";
import { DepartmentSelect, DoctorSelect, PatientSelect } from "../../components/common/Pickers";
import { BackLink, NoPatientRecord } from "../../components/common/RecordStates";
import { useAuth } from "../../context/AuthContext";
import { useForm } from "../../hooks/useForm";
import { useMutation } from "../../hooks/useMutation";
import { appointmentApi } from "../../api/appointmentApi";
import { compact } from "../../utils/payload";
import { todayISO } from "../../utils/formatDate";

export default function CreateAppointment() {
  const navigate = useNavigate();
  const { isPatient, patientId } = useAuth();
  const form = useForm({ patientId: "", doctorId: "", departmentId: "", appointmentDate: "", startTime: "", endTime: "", reason: "", notes: "" });
  const save = useMutation(appointmentApi.create, { success: "Appointment booked." });

  if (isPatient && !patientId) return <NoPatientRecord />;

  const submit = async (event) => {
    event.preventDefault();
    const v = form.values;
    if (v.endTime <= v.startTime) {
      form.setErrors({ endTime: "End time must be after the start time." });
      return;
    }
    const body = compact({ ...v, patientId: isPatient ? patientId : v.patientId });
    const res = await save.run(body);
    if (res) navigate(res.data?.id ? `/app/appointments/${res.data.id}` : "/app/appointments");
  };

  return (
    <>
      <BackLink to="/app/appointments">Appointments</BackLink>
      <PageHeader eyebrow="Appointments" title="Book an appointment" />
      <form onSubmit={submit} className="surface space-y-5 p-6">
        {!isPatient && (
          <PatientSelect required value={form.values.patientId} onChange={(id) => form.set("patientId", id)} error={form.errors.patientId || save.fieldErrors.patientId} />
        )}
        <div className="grid gap-4 md:grid-cols-2">
          <DoctorSelect required value={form.values.doctorId} onChange={(id) => form.set("doctorId", id)} error={save.fieldErrors.doctorId} />
          <DepartmentSelect value={form.values.departmentId} onChange={(id) => form.set("departmentId", id)} error={save.fieldErrors.departmentId} />
        </div>
        <div className="grid gap-4 md:grid-cols-3">
          <Input label="Date" type="date" required min={todayISO()} {...form.bind("appointmentDate", save.fieldErrors)} />
          <Input label="Start time" type="time" required {...form.bind("startTime", save.fieldErrors)} />
          <Input label="End time" type="time" required {...form.bind("endTime", save.fieldErrors)} />
        </div>
        <Input label="Reason for visit" maxLength={500} {...form.bind("reason", save.fieldErrors)} />
        <Textarea label="Notes" {...form.bind("notes", save.fieldErrors)} />
        <FormActions loading={save.loading} cancelTo="/app/appointments" submitLabel="Book appointment" />
      </form>
    </>
  );
}

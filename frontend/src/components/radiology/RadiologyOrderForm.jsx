import Input from "../common/Input";
export default function RadiologyOrderForm({ form, setForm }) {
  return (
    <div className="grid gap-4 md:grid-cols-2">
      <Input
        label="Patient or record ID"
        value={form?.patientId || ""}
        onChange={(e) =>
          setForm && setForm({ ...form, patientId: e.target.value })
        }
      />
      <Input
        label="Notes"
        value={form?.notes || ""}
        onChange={(e) => setForm && setForm({ ...form, notes: e.target.value })}
      />
    </div>
  );
}

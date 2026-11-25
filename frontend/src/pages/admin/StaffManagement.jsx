import { useMemo, useState } from "react";
import PageHeader from "../../components/common/PageHeader";
import DataTable from "../../components/common/DataTable";
import EmptyState from "../../components/common/EmptyState";
import Async from "../../components/common/Async";
import Segmented from "../../components/common/Segmented";
import SearchBar from "../../components/common/SearchBar";
import Select from "../../components/common/Select";
import Input from "../../components/common/Input";
import Button from "../../components/common/Button";
import { useApi } from "../../hooks/useApi";
import { useMutation } from "../../hooks/useMutation";
import { adminApi } from "../../api/adminApi";
import { directoryApi } from "../../api/directoryApi";
import { asList, personName } from "../../utils/payload";
import { roleLabel } from "../../utils/roles";
import StatusBadge from "../../components/common/StatusBadge";

const ROLES = ["ALL", "DOCTOR", "NURSE", "LAB_TECHNICIAN", "RADIOLOGY_TECHNICIAN", "PHARMACIST"];
const blank = { firstName: "", lastName: "", email: "", phone: "", role: "DOCTOR", departmentId: "", initialPassword: "" };

export default function StaffManagement() {
  const [filter, setFilter] = useState("ALL");
  const [query, setQuery] = useState("");
  const [form, setForm] = useState(blank);
  const [open, setOpen] = useState(false);
  const state = useApi(() => adminApi.staff(), []);
  const departments = useApi(() => directoryApi.departments(), []);
  const save = useMutation(adminApi.createStaff, { success: "Staff account created." });
  const updateStatus = useMutation((id, status) => adminApi.updateStaffStatus(id, status), { success: "Account status updated." });
  const staff = useMemo(() => asList(state.data).filter((u) => {
    const roleMatch = filter === "ALL" || (u.roles || []).includes(filter);
    const text = `${personName(u)} ${u.email}`.toLowerCase();
    return roleMatch && text.includes(query.toLowerCase());
  }), [state.data, filter, query]);
  const set = (key, value) => setForm((current) => ({ ...current, [key]: value }));
  const submit = async (event) => {
    event.preventDefault();
    const response = await save.run({ ...form, departmentId: form.departmentId ? Number(form.departmentId) : null });
    if (response) { setForm(blank); setOpen(false); state.reload(); }
  };

  return <>
    <PageHeader eyebrow="Administration" title="Staff management" description="Create and manage hospital staff accounts and roles." actions={<Button onClick={() => setOpen(!open)}>+ Add staff</Button>} />
    {open && <form onSubmit={submit} className="surface mb-6 grid gap-4 p-6 md:grid-cols-2">
      <Input label="First name" required value={form.firstName} onChange={(e) => set("firstName", e.target.value)} />
      <Input label="Last name" value={form.lastName} onChange={(e) => set("lastName", e.target.value)} />
      <Input label="Email" type="email" required value={form.email} onChange={(e) => set("email", e.target.value)} />
      <Input label="Phone" value={form.phone} onChange={(e) => set("phone", e.target.value)} />
      <Select label="Role" value={form.role} onChange={(e) => set("role", e.target.value)}>{ROLES.slice(1).map((r) => <option key={r}>{r}</option>)}</Select>
      <Select label="Department" value={form.departmentId} onChange={(e) => set("departmentId", e.target.value)}><option value="">None</option>{asList(departments.data).map((d) => <option key={d.id} value={d.id}>{d.name}</option>)}</Select>
      <Input label="Initial password" type="password" minLength={8} required value={form.initialPassword} onChange={(e) => set("initialPassword", e.target.value)} />
      <div className="flex items-end gap-3"><Button type="submit" loading={save.loading}>Create staff</Button><Button type="button" variant="secondary" onClick={() => setOpen(false)}>Cancel</Button></div>
    </form>}
    <div className="surface p-5"><div className="mb-4 flex flex-wrap gap-4"><div className="w-full max-w-sm"><SearchBar value={query} onChange={setQuery} placeholder="Search staff…" /></div><Segmented options={ROLES.map((r) => ({ value: r, label: r === "ALL" ? "All" : roleLabel(r) }))} value={filter} onChange={setFilter} /></div>
      <Async state={state}>{() => <DataTable rows={staff} empty={<EmptyState title="No staff found" />} columns={[
        { key: "name", label: "Name", render: (u) => personName(u) || "—" },
        { key: "email", label: "Email" },
        { key: "role", label: "Role", render: (u) => (u.roles || []).map(roleLabel).join(", ") },
        { key: "department", label: "Department", render: (u) => u.departmentId || "—" },
        { key: "status", label: "Status", render: (u) => <StatusBadge status={u.accountStatus} /> },
        { key: "actions", label: "Actions", render: (u) => <Button variant="secondary" onClick={() => updateStatus.run(u.userId, u.accountStatus === "ACTIVE" ? "SUSPENDED" : "ACTIVE")} loading={updateStatus.loading}>{u.accountStatus === "ACTIVE" ? "Suspend" : "Activate"}</Button> },
      ]} />}</Async>
    </div>
  </>;
}
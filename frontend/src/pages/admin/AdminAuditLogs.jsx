import { useState } from "react";
import PageHeader from "../../components/common/PageHeader";
import DataTable from "../../components/common/DataTable";
import EmptyState from "../../components/common/EmptyState";
import Async from "../../components/common/Async";
import Select from "../../components/common/Select";
import Input from "../../components/common/Input";
import Button from "../../components/common/Button";
import { useApi } from "../../hooks/useApi";
import { auditApi } from "../../api/auditApi";
import { asList } from "../../utils/payload";
import { formatDateTime } from "../../utils/formatDate";

const pad = (n) => String(n).padStart(2, "0");
const local = (d) => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
const withSeconds = (v) => (v.length === 16 ? `${v}:00` : v);

const MODES = [
  { value: "range", label: "Date range" },
  { value: "user", label: "By user ID" },
  { value: "patient", label: "By patient ID" },
  { value: "action", label: "By action" },
  { value: "resource", label: "By resource" },
];

export default function AdminAuditLogs() {
  const now = new Date();
  const [mode, setMode] = useState("range");
  const [draft, setDraft] = useState({ start: local(new Date(now.getTime() - 7 * 864e5)), end: local(now), id: "", action: "", type: "", resourceId: "" });
  const [applied, setApplied] = useState(draft);
  const set = (k) => (e) => setDraft({ ...draft, [k]: e.target.value });

  const ready = { range: applied.start && applied.end, user: applied.id, patient: applied.id, action: applied.action, resource: applied.type && applied.resourceId }[mode];
  const state = useApi(() => {
    if (mode === "user") return auditApi.byUser(applied.id);
    if (mode === "patient") return auditApi.byPatient(applied.id);
    if (mode === "action") return auditApi.byAction(applied.action.trim());
    if (mode === "resource") return auditApi.byResource(applied.type.trim(), applied.resourceId);
    return auditApi.byDateRange({ start: withSeconds(applied.start), end: withSeconds(applied.end) });
  }, [mode, applied], Boolean(ready));

  return (
    <>
      <PageHeader eyebrow="Administration" title="Audit logs" description="Who accessed or changed what, and when." />
      <form onSubmit={(e) => { e.preventDefault(); setApplied(draft); }} className="surface mb-5 flex flex-wrap items-end gap-4 p-5">
        <Select label="Search" value={mode} onChange={(e) => setMode(e.target.value)} className="w-48">
          {MODES.map((m) => <option key={m.value} value={m.value}>{m.label}</option>)}
        </Select>
        {mode === "range" && (
          <>
            <Input label="From" type="datetime-local" value={draft.start} onChange={set("start")} />
            <Input label="To" type="datetime-local" value={draft.end} onChange={set("end")} />
          </>
        )}
        {(mode === "user" || mode === "patient") && <Input label={mode === "user" ? "User ID" : "Patient ID"} type="number" min={1} value={draft.id} onChange={set("id")} />}
        {mode === "action" && <Input label="Action" list="audit-actions" placeholder="e.g. VIEW" value={draft.action} onChange={set("action")} />}
        {mode === "resource" && (
          <>
            <Input label="Resource type" placeholder="e.g. PATIENT" value={draft.type} onChange={set("type")} />
            <Input label="Resource ID" type="number" min={1} value={draft.resourceId} onChange={set("resourceId")} />
          </>
        )}
        <datalist id="audit-actions">{["VIEW", "CREATE", "UPDATE", "DELETE", "LOGIN"].map((a) => <option key={a} value={a} />)}</datalist>
        <Button type="submit">Search logs</Button>
      </form>
      <div className="surface p-5">
        {!ready ? (
          <EmptyState title="Enter a search" message="Fill in the search fields above." />
        ) : (
          <Async state={state}>
            {(data) => (
              <DataTable
                rows={asList(data)}
                empty={<EmptyState title="No log entries" message="Nothing matches this search." />}
                columns={[
                  { key: "createdAt", label: "When", render: (l) => formatDateTime(l.createdAt) },
                  { key: "action", label: "Action" },
                  { key: "resource", label: "Resource", render: (l) => [l.resourceType, l.resourceId].filter(Boolean).join(" #") || "—" },
                  { key: "userId", label: "User" },
                  { key: "patientId", label: "Patient" },
                  { key: "description", label: "Description" },
                  { key: "ipAddress", label: "IP" },
                ]}
              />
            )}
          </Async>
        )}
      </div>
    </>
  );
}

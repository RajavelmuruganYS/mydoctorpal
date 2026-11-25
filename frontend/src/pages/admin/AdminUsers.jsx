import { useState } from "react";
import { Link } from "react-router-dom";
import PageHeader from "../../components/common/PageHeader";
import DataTable from "../../components/common/DataTable";
import EmptyState from "../../components/common/EmptyState";
import Async from "../../components/common/Async";
import Segmented from "../../components/common/Segmented";
import SearchBar from "../../components/common/SearchBar";
import StatusBadge from "../../components/common/StatusBadge";
import { useApi } from "../../hooks/useApi";
import { adminApi } from "../../api/adminApi";
import { patientApi } from "../../api/patientApi";
import { asList, personName } from "../../utils/payload";
import { matches } from "../../utils/search";
import { roleLabel } from "../../utils/roles";

const ROLE_FILTERS = ["ALL", "PATIENT", "DOCTOR", "NURSE", "LAB_TECHNICIAN", "RADIOLOGY_TECHNICIAN", "PHARMACIST", "ADMIN"];

export default function AdminUsers() {
  const [role, setRole] = useState("ALL");
  const [query, setQuery] = useState("");
  const state = useApi(() => (role === "ALL" ? adminApi.users() : adminApi.byRole(role)), [role]);
  const patients = useApi(() => patientApi.list(), []);
  const withRecord = new Set(asList(patients.data).map((p) => p.userId));

  return (
    <>
      <PageHeader eyebrow="Administration" title="Users" description="All accounts registered in the system." />
      <div className="surface p-5">
        <div className="mb-4 flex flex-wrap items-center gap-4">
          <div className="w-full max-w-sm"><SearchBar value={query} onChange={setQuery} placeholder="Search name or email…" /></div>
          <Segmented options={ROLE_FILTERS.map((r) => ({ value: r, label: r === "ALL" ? "All" : roleLabel(r) }))} value={role} onChange={setRole} />
        </div>
        <Async state={state}>
          {(data) => (
            <DataTable
              rows={asList(data).filter((u) => matches({ ...u, name: personName(u) }, ["name", "email"], query))}
              empty={<EmptyState title="No users found" />}
              columns={[
                { key: "name", label: "Name", render: (u) => personName(u) || "—" },
                { key: "email", label: "Email" },
                { key: "roles", label: "Roles", render: (u) => (u.roles || []).map(roleLabel).join(", ") },
                { key: "accountStatus", label: "Status", render: (u) => <StatusBadge status={u.accountStatus} /> },
                {
                  key: "record",
                  label: "Patient record",
                  render: (u) =>
                    (u.roles || []).includes("PATIENT") ? (
                      withRecord.has(u.userId) ? (
                        <StatusBadge status="ACTIVE" />
                      ) : (
                        <Link to={`/app/patients/new?userId=${u.userId}`} className="btn-secondary !px-3 !py-1.5 text-xs">Create record</Link>
                      )
                    ) : (
                      "—"
                    ),
                },
              ]}
            />
          )}
        </Async>
      </div>
    </>
  );
}

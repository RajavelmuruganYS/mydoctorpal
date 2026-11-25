import { useState } from "react";
import { Link } from "react-router-dom";
import { Plus } from "lucide-react";
import PageHeader from "../../components/common/PageHeader";
import SearchBar from "../../components/common/SearchBar";
import DataTable from "../../components/common/DataTable";
import EmptyState from "../../components/common/EmptyState";
import Async from "../../components/common/Async";
import { useApi } from "../../hooks/useApi";
import { useAuth } from "../../context/AuthContext";
import { patientApi } from "../../api/patientApi";
import { directoryApi } from "../../api/directoryApi";
import { asList, personName } from "../../utils/payload";
import { matches } from "../../utils/search";
import { formatDate } from "../../utils/formatDate";

export default function Patients() {
  const { hasRole, role } = useAuth();
  const [query, setQuery] = useState("");
  const state = useApi(
    () => role === "DOCTOR" ? directoryApi.assignedPatients() : patientApi.list(),
    [role],
  );

  return (
    <>
      <PageHeader
        eyebrow="Patients"
        title="Patient registry"
        description={role === "DOCTOR" ? "Patients assigned to you. Select a patient to see their full chart." : "Every patient record in the hospital. Select a patient to see their full chart."}
        actions={
          hasRole("ADMIN") && (
            <Link to="/app/patients/new" className="btn-primary">
              <Plus size={16} /> Create patient record
            </Link>
          )
        }
      />
      <div className="surface p-5">
        <div className="mb-4 max-w-md">
          <SearchBar value={query} onChange={setQuery} placeholder="Search by name, number or email…" />
        </div>
        <Async state={state}>
          {(data) => {
            const rows = asList(data).filter((p) =>
              matches({ ...p, name: personName(p) }, ["name", "patientNumber", "email", "phone"], query),
            );
            return (
              <DataTable
                rows={rows}
                rowLink={(p) => `/app/patients/${p.id}`}
                empty={<EmptyState title="No patients found" message={query ? "Try a different search." : "No patient records exist yet."} />}
                columns={[
                  { key: "name", label: "Patient", render: (p) => personName(p) || "(no name)" },
                  { key: "patientNumber", label: "Patient no." },
                  { key: "gender", label: "Gender" },
                  { key: "dateOfBirth", label: "Date of birth", render: (p) => formatDate(p.dateOfBirth) },
                  { key: "bloodGroup", label: "Blood group" },
                  { key: "phone", label: "Phone" },
                ]}
              />
            );
          }}
        </Async>
      </div>
    </>
  );
}

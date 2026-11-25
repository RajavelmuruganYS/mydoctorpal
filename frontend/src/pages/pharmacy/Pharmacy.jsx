import { useState } from "react";
import { Link } from "react-router-dom";
import PageHeader from "../../components/common/PageHeader";
import DataTable from "../../components/common/DataTable";
import EmptyState from "../../components/common/EmptyState";
import Async from "../../components/common/Async";
import StatusBadge from "../../components/common/StatusBadge";
import { PatientNumberLookup } from "../../components/common/Pickers";
import { useApi } from "../../hooks/useApi";
import { useAuth } from "../../context/AuthContext";
import { pharmacyApi } from "../../api/pharmacyApi";
import { prescriptionApi } from "../../api/prescriptionApi";
import { asList, personName } from "../../utils/payload";
import { formatDate, formatDateTime } from "../../utils/formatDate";

function DispenseQueue() {
  const [patient, setPatient] = useState(null);
  const state = useApi(() => prescriptionApi.byPatient(patient.id), [patient?.id], Boolean(patient));

  return (
    <div className="surface mb-6 p-6">
      <h3 className="mb-1 font-bold">Find a prescription to dispense</h3>
      <p className="mb-4 text-sm muted">Look up the patient by their patient number, then open a signed prescription.</p>
      <PatientNumberLookup onFound={setPatient} />
      {patient && (
        <div className="mt-5">
          <p className="mb-3 text-sm font-semibold">{personName(patient)} · {patient.patientNumber}</p>
          <Async state={state} inline>
            {(data) => (
              <DataTable
                rows={asList(data)}
                empty={<EmptyState title="No prescriptions" message="This patient has no prescriptions." />}
                columns={[
                  { key: "id", label: "Prescription", render: (p) => `#${p.id}` },
                  { key: "prescriptionDate", label: "Date", render: (p) => formatDate(p.prescriptionDate) },
                  { key: "diagnosisCode", label: "Diagnosis" },
                  { key: "status", label: "Status", render: (p) => <StatusBadge status={p.status} /> },
                  { key: "dispenseStatus", label: "Dispensing", render: (p) => <StatusBadge status={p.dispenseStatus} /> },
                  {
                    key: "action",
                    label: "",
                    render: (p) =>
                      p.status === "SIGNED" && p.dispenseStatus !== "DISPENSED" ? (
                        <Link to={`/app/pharmacy/dispense/${p.id}`} className="btn-primary !px-4 !py-2 text-xs">Dispense</Link>
                      ) : (
                        <Link to={`/app/prescriptions/${p.id}`} className="text-xs font-bold text-forest">View</Link>
                      ),
                  },
                ]}
              />
            )}
          </Async>
        </div>
      )}
    </div>
  );
}

export default function Pharmacy() {
  const { role, userId } = useAuth();
  const isPharmacist = role === "PHARMACIST";
  const state = useApi(() => (isPharmacist ? pharmacyApi.byPharmacist(userId) : pharmacyApi.byStatus("DISPENSED")), [isPharmacist, userId]);

  return (
    <>
      <PageHeader eyebrow="Pharmacy" title="Pharmacy desk" description={isPharmacist ? "Your dispensing history and the prescription lookup." : "All dispensed prescriptions."} />
      {isPharmacist && <DispenseQueue />}
      <div className="surface p-5">
        <h3 className="mb-4 font-bold">{isPharmacist ? "My dispensations" : "Dispensations"}</h3>
        <Async state={state}>
          {(data) => (
            <DataTable
              rows={asList(data)}
              rowLink={(d) => `/app/pharmacy/dispensations/${d.id}`}
              empty={<EmptyState title="Nothing dispensed yet" message="Dispensations will appear here." />}
              columns={[
                { key: "id", label: "Dispensation", render: (d) => `#${d.id}` },
                { key: "prescriptionId", label: "Prescription", render: (d) => `#${d.prescriptionId}` },
                { key: "dispensedAt", label: "Dispensed", render: (d) => formatDateTime(d.dispensedAt) },
                { key: "status", label: "Status", render: (d) => <StatusBadge status={d.status} /> },
                { key: "notes", label: "Notes" },
              ]}
            />
          )}
        </Async>
      </div>
    </>
  );
}

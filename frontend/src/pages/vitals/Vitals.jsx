import { Link } from "react-router-dom";
import { Plus } from "lucide-react";
import { CartesianGrid, Legend, Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import PageHeader from "../../components/common/PageHeader";
import DataTable from "../../components/common/DataTable";
import EmptyState from "../../components/common/EmptyState";
import Async from "../../components/common/Async";
import { PatientScopeBar, usePatientScope } from "../../components/common/Pickers";
import { NoPatientRecord } from "../../components/common/RecordStates";
import { useApi } from "../../hooks/useApi";
import { useAuth } from "../../context/AuthContext";
import { vitalsApi } from "../../api/vitalsApi";
import { asList } from "../../utils/payload";
import { formatDateTime } from "../../utils/formatDate";

const bp = (v) => (v.bpSystolic != null ? `${v.bpSystolic}/${v.bpDiastolic ?? "–"}` : "—");
const cell = (value, unit = "") => (value === null || value === undefined ? "—" : `${value}${unit}`);

export default function Vitals() {
  const { isPatient, patientId: ownId, hasRole } = useAuth();
  const scope = usePatientScope();
  const patientId = isPatient ? ownId : scope.patientId;
  const state = useApi(() => vitalsApi.byPatient(patientId), [patientId], Boolean(patientId));

  if (isPatient && !ownId) return <NoPatientRecord />;

  return (
    <>
      <PageHeader
        eyebrow="Vitals"
        title={isPatient ? "My vitals" : "Patient vitals"}
        actions={hasRole("DOCTOR", "NURSE") && <Link to={`/app/vitals/new${patientId ? `?patient=${patientId}` : ""}`} className="btn-primary"><Plus size={16} /> Record vitals</Link>}
      />
      <PatientScopeBar scope={scope} />
      {!patientId ? (
        <EmptyState title="Choose a patient" message="Select a patient to see their vital signs." />
      ) : (
        <Async state={state}>
          {(data) => {
            const rows = asList(data).sort((a, b) => String(b.recordedAt).localeCompare(String(a.recordedAt)));
            if (!rows.length) return <EmptyState title="No vitals recorded" message="No vital signs have been recorded for this patient yet." />;
            const latest = rows[0];
            const chart = [...rows].reverse().map((v) => ({
              time: formatDateTime(v.recordedAt),
              Pulse: v.pulseRate,
              Systolic: v.bpSystolic,
              Diastolic: v.bpDiastolic,
              "SpO₂": v.spo2 == null ? null : Number(v.spo2),
            }));
            return (
              <>
                <div className="mb-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-5">
                  {[
                    ["Temperature", cell(latest.temperature, " °C")],
                    ["Blood pressure", bp(latest)],
                    ["Pulse", cell(latest.pulseRate, " bpm")],
                    ["SpO₂", cell(latest.spo2, " %")],
                    ["Respiratory rate", cell(latest.respiratoryRate, " /min")],
                  ].map(([label, value]) => (
                    <div key={label} className="surface p-5">
                      <p className="text-xs font-bold uppercase tracking-wider text-forest/50">{label}</p>
                      <p className="mt-2 font-display text-2xl font-extrabold">{value}</p>
                    </div>
                  ))}
                </div>
                <p className="-mt-3 mb-6 text-xs muted">Latest reading recorded {formatDateTime(latest.recordedAt)}</p>
                {rows.length > 1 && (
                  <div className="surface mb-6 p-5">
                    <h3 className="mb-4 font-bold">Trends</h3>
                    <div className="h-72">
                      <ResponsiveContainer width="100%" height="100%">
                        <LineChart data={chart}>
                          <CartesianGrid strokeDasharray="3 3" />
                          <XAxis dataKey="time" tick={{ fontSize: 11 }} />
                          <YAxis tick={{ fontSize: 11 }} />
                          <Tooltip />
                          <Legend />
                          <Line type="monotone" dataKey="Pulse" stroke="#2f6f4f" connectNulls />
                          <Line type="monotone" dataKey="Systolic" stroke="#e76f51" connectNulls />
                          <Line type="monotone" dataKey="Diastolic" stroke="#f4a261" connectNulls />
                          <Line type="monotone" dataKey="SpO₂" stroke="#457b9d" connectNulls />
                        </LineChart>
                      </ResponsiveContainer>
                    </div>
                  </div>
                )}
                <div className="surface p-5">
                  <DataTable
                    rows={rows}
                    columns={[
                      { key: "recordedAt", label: "Recorded", render: (v) => formatDateTime(v.recordedAt) },
                      { key: "temperature", label: "Temp", render: (v) => cell(v.temperature, " °C") },
                      { key: "bp", label: "BP", render: bp },
                      { key: "pulseRate", label: "Pulse", render: (v) => cell(v.pulseRate) },
                      { key: "spo2", label: "SpO₂", render: (v) => cell(v.spo2, " %") },
                      { key: "respiratoryRate", label: "Resp. rate", render: (v) => cell(v.respiratoryRate) },
                    ]}
                  />
                </div>
              </>
            );
          }}
        </Async>
      )}
    </>
  );
}

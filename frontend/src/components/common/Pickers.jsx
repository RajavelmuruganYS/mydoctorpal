import { useState } from "react";
import { useSearchParams } from "react-router-dom";
import Select from "./Select";
import Input from "./Input";
import Button from "./Button";
import { useAuth } from "../../context/AuthContext";
import { useDirectory } from "../../hooks/useDirectory";
import { useApi } from "../../hooks/useApi";
import { useToast } from "../../context/ToastContext";
import { patientApi } from "../../api/patientApi";
import { prescriptionApi } from "../../api/prescriptionApi";
import { asList, personName } from "../../utils/payload";
import { getApiError } from "../../utils/errorHandler";
import { CAN_LIST_PATIENTS } from "../../utils/permissions";

const patientLabel = (p) =>
  `${personName(p) || "Patient"} — ${p.patientNumber}`;

/** Find one patient by patient number (for roles that may not list all patients). */
export function PatientNumberLookup({ onFound, label = "Patient number" }) {
  const toast = useToast();
  const [number, setNumber] = useState("");
  const [busy, setBusy] = useState(false);

  const search = async (event) => {
    event.preventDefault();
    if (!number.trim()) return;
    setBusy(true);
    try {
      const { data } = await patientApi.byNumber(number.trim());
      onFound(data);
    } catch (error) {
      toast.error(getApiError(error));
    } finally {
      setBusy(false);
    }
  };

  return (
    <form onSubmit={search} className="flex items-end gap-3">
      <Input
        label={label}
        value={number}
        onChange={(e) => setNumber(e.target.value)}
        placeholder="e.g. PAT-2026-000001"
        className="flex-1"
      />
      <Button type="submit" loading={busy}>
        Find patient
      </Button>
    </form>
  );
}

/**
 * Patient chooser. Doctors, nurses and admins pick from the patient list;
 * other staff (lab, pharmacy) look a patient up by number.
 * onChange(patientId:number|"" , patient|undefined)
 */
export function PatientSelect({ value, onChange, label = "Patient", required, error }) {
  const { hasRole } = useAuth();
  const { patients, loading } = useDirectory();

  if (!hasRole(CAN_LIST_PATIENTS)) {
    return (
      <div>
        <PatientNumberLookup onFound={(p) => onChange(p.id, p)} label={label} />
        {value ? <p className="mt-2 text-xs font-semibold text-forest">Selected patient ID: {value}</p> : null}
        {error && <p className="mt-1 text-xs font-semibold text-rose-600">{error}</p>}
      </div>
    );
  }

  return (
    <Select
      label={label}
      required={required}
      error={error}
      value={value ?? ""}
      onChange={(e) => {
        const id = e.target.value ? Number(e.target.value) : "";
        onChange(id, patients.find((p) => p.id === id));
      }}
    >
      <option value="">{loading ? "Loading patients…" : "Select a patient…"}</option>
      {patients.map((p) => (
        <option key={p.id} value={p.id}>
          {patientLabel(p)}
        </option>
      ))}
    </Select>
  );
}

export function DoctorSelect({ value, onChange, label = "Doctor", required, error }) {
  const { doctors, loading } = useDirectory();
  return (
    <Select
      label={label}
      required={required}
      error={error}
      value={value ?? ""}
      onChange={(e) => onChange(e.target.value ? Number(e.target.value) : "")}
    >
      <option value="">{loading ? "Loading doctors…" : "Select a doctor…"}</option>
      {doctors.map((d) => (
        <option key={d.userId} value={d.userId}>
          Dr. {personName(d)}
        </option>
      ))}
    </Select>
  );
}

export function DepartmentSelect({ value, onChange, label = "Department", required, error }) {
  const { departments, loading } = useDirectory();
  return (
    <Select
      label={label}
      required={required}
      error={error}
      value={value ?? ""}
      onChange={(e) => onChange(e.target.value ? Number(e.target.value) : "")}
    >
      <option value="">{loading ? "Loading departments…" : "Select a department…"}</option>
      {departments.map((d) => (
        <option key={d.id} value={d.id}>
          {d.name}
        </option>
      ))}
    </Select>
  );
}

/** Active drugs from the catalogue. onChange(drugId, drug) */
export function DrugSelect({ value, onChange, label = "Drug", required, error }) {
  const { data, loading } = useApi(() => prescriptionApi.drugs(), []);
  const drugs = asList(data).filter((d) => d.active);
  return (
    <Select
      label={label}
      required={required}
      error={error}
      value={value ?? ""}
      onChange={(e) => {
        const id = e.target.value ? Number(e.target.value) : "";
        onChange(id, drugs.find((d) => d.id === id));
      }}
    >
      <option value="">{loading ? "Loading drugs…" : "Select a drug…"}</option>
      {drugs.map((d) => (
        <option key={d.id} value={d.id}>
          {d.brandName} ({d.genericName}) — {d.strength}
        </option>
      ))}
    </Select>
  );
}

/**
 * Which patient's records is a list page showing?
 *  - a PATIENT user always sees their own (patientId from /patients/me)
 *  - staff choose one; the choice lives in the URL (?patient=ID) so it can be shared
 */
export function usePatientScope() {
  const { isPatient, patientId: ownId } = useAuth();
  const [params, setParams] = useSearchParams();
  const selected = params.get("patient");
  const patientId = isPatient ? ownId : selected ? Number(selected) : null;

  const setPatient = (id) => {
    const next = new URLSearchParams(params);
    if (id) next.set("patient", String(id));
    else next.delete("patient");
    setParams(next, { replace: true });
  };

  return { patientId, isSelf: isPatient, setPatient };
}

/** Picker bar for staff list pages (renders nothing for patients). */
export function PatientScopeBar({ scope, hint = "Choose a patient to view their records." }) {
  const { patientId, isSelf, setPatient } = scope;
  const selected = useApi(() => patientApi.get(patientId), [patientId], Boolean(patientId) && !isSelf);
  if (isSelf) return null;

  return (
    <div className="surface mb-5 p-4">
      <div className="grid gap-4 md:grid-cols-[1fr_auto] md:items-end">
        <PatientSelect
          label="Patient"
          value={patientId || ""}
          onChange={(id) => setPatient(id)}
        />
        {patientId ? (
          <Button variant="secondary" onClick={() => setPatient(null)}>
            Clear
          </Button>
        ) : null}
      </div>
      <p className="mt-2 text-xs muted">
        {patientId && selected.data
          ? `Showing records for ${personName(selected.data) || "patient"} (${selected.data.patientNumber}).`
          : hint}
      </p>
    </div>
  );
}

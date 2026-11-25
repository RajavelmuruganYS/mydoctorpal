import EmptyState from "./EmptyState";
import { Link } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

/** Shown to a PATIENT user whose account has no patient record yet. */
export function NoPatientRecord() {
  const { patientState, reloadPatient } = useAuth();
  if (patientState.loading) return <div className="p-8 text-center muted">Loading your record…</div>;
  return (
    <EmptyState
      title="Your patient record is not set up yet"
      message="A hospital administrator needs to create your patient record before clinical data can be shown. Please contact the front desk, then reload."
      action={
        <button className="btn-secondary mt-4" onClick={reloadPatient}>
          Check again
        </button>
      }
    />
  );
}

export function BackLink({ to, children }) {
  return (
    <Link to={to} className="mb-4 inline-block text-sm font-bold text-forest hover:underline">
      ← {children}
    </Link>
  );
}

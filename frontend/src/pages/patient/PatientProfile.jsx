import PageHeader from "../../components/common/PageHeader";
import { NoPatientRecord } from "../../components/common/RecordStates";
import { PatientRecord } from "./PatientDetails";
import { useAuth } from "../../context/AuthContext";
import { personName } from "../../utils/payload";

export default function PatientProfile() {
  const { patient } = useAuth();
  if (!patient) return <NoPatientRecord />;
  return (
    <>
      <PageHeader
        eyebrow="My profile"
        title={personName(patient) || "My health profile"}
        description="Your patient record. To correct any detail, ask your care team — patients cannot edit clinical records directly."
      />
      <div className="surface p-6">
        <PatientRecord patient={patient} />
      </div>
    </>
  );
}

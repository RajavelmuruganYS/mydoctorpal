import PageHeader from "../../components/common/PageHeader";
import DetailGrid from "../../components/common/DetailGrid";
import { PatientRecord } from "../patient/PatientDetails";
import { useAuth } from "../../context/AuthContext";
import { roleLabel } from "../../utils/roles";
import { personName } from "../../utils/payload";
import { formatDateTime } from "../../utils/formatDate";

export default function MyProfile() {
  const { user, roles, patient } = useAuth();
  return (
    <>
      <PageHeader eyebrow="Account" title={personName(user) || "My account"} description="Your sign-in account details." />
      <div className="surface p-6">
        <DetailGrid
          items={[
            { label: "Name", value: personName(user) },
            { label: "Email", value: user.email },
            { label: "Phone", value: user.phone },
            { label: "Roles", value: roles.map(roleLabel).join(", ") },
            { label: "Account status", value: user.accountStatus },
            { label: "Last login", value: formatDateTime(user.lastLoginAt) },
          ]}
        />
      </div>
      {patient && (
        <div className="surface mt-6 p-6">
          <h3 className="mb-4 font-bold">Patient record</h3>
          <PatientRecord patient={patient} />
        </div>
      )}
    </>
  );
}

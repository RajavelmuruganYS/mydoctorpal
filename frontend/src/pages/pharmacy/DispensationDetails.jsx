import { Link, useParams } from "react-router-dom";
import PageHeader from "../../components/common/PageHeader";
import Async from "../../components/common/Async";
import DetailGrid from "../../components/common/DetailGrid";
import StatusBadge from "../../components/common/StatusBadge";
import { BackLink } from "../../components/common/RecordStates";
import { useApi } from "../../hooks/useApi";
import { pharmacyApi } from "../../api/pharmacyApi";
import { formatDateTime } from "../../utils/formatDate";

export default function DispensationDetails() {
  const { id } = useParams();
  const state = useApi(() => pharmacyApi.getDispensation(id), [id]);
  return (
    <>
      <BackLink to="/app/pharmacy">Pharmacy</BackLink>
      <Async state={state}>
        {(d) => (
          <>
            <PageHeader eyebrow="Dispensation" title={`Dispensation #${d.id}`} actions={<StatusBadge status={d.status} />} />
            <div className="surface p-6">
              <DetailGrid
                items={[
                  { label: "Prescription", value: <Link className="underline" to={`/app/prescriptions/${d.prescriptionId}`}>#{d.prescriptionId}</Link> },
                  { label: "Pharmacist (user ID)", value: d.pharmacistId },
                  { label: "Dispensed at", value: formatDateTime(d.dispensedAt) },
                  { label: "Notes", value: d.notes },
                ]}
              />
            </div>
          </>
        )}
      </Async>
    </>
  );
}

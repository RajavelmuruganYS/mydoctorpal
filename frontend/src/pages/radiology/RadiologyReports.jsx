import { useState } from "react";
import { Link } from "react-router-dom";
import PageHeader from "../../components/common/PageHeader";
import DataTable from "../../components/common/DataTable";
import EmptyState from "../../components/common/EmptyState";
import Async from "../../components/common/Async";
import Segmented from "../../components/common/Segmented";
import StatusBadge from "../../components/common/StatusBadge";
import { BackLink } from "../../components/common/RecordStates";
import { useApi } from "../../hooks/useApi";
import { useAuth } from "../../context/AuthContext";
import { radiologyApi } from "../../api/radiologyApi";
import { toArray } from "./radiologyData";
import { formatDateTime } from "../../utils/formatDate";
import { REPORT_STATUSES } from "../../utils/constants";

export default function RadiologyReports() {
  const { role, userId } = useAuth();
  const [view, setView] = useState("DRAFT");
  const options = [...REPORT_STATUSES, "CRITICAL", ...(role === "DOCTOR" ? ["MINE"] : [])];
  const state = useApi(() => {
    if (view === "CRITICAL") return radiologyApi.criticalReports();
    if (view === "MINE") return radiologyApi.reportsByUser(userId);
    return radiologyApi.reportsByStatus(view);
  }, [view, userId]);

  return (
    <>
      <BackLink to="/app/radiology">Radiology</BackLink>
      <PageHeader eyebrow="Radiology" title="Radiology reports" />
      <div className="surface p-5">
        <div className="mb-4">
          <Segmented options={options.map((o) => ({ value: o, label: o === "MINE" ? "My reports" : o === "CRITICAL" ? "Critical findings" : o }))} value={view} onChange={setView} />
        </div>
        <Async state={state}>
          {(data) => (
            <DataTable
              rows={toArray(data)}
              empty={<EmptyState title="No reports" message="No reports in this view." />}
              columns={[
                { key: "id", label: "Report", render: (r) => `#${r.id}` },
                { key: "radiologyOrderId", label: "Order", render: (r) => <Link className="font-bold underline" to={`/app/radiology/orders/${r.radiologyOrderId}`}>#{r.radiologyOrderId}</Link> },
                { key: "impression", label: "Impression", render: (r) => <span className="line-clamp-2">{r.impression || r.reportText}</span> },
                { key: "criticalFinding", label: "Critical", render: (r) => (r.criticalFinding ? <StatusBadge status="CRITICAL" /> : "—") },
                { key: "signedAt", label: "Signed", render: (r) => formatDateTime(r.signedAt) },
                { key: "status", label: "Status", render: (r) => <StatusBadge status={r.status} /> },
              ]}
            />
          )}
        </Async>
      </div>
    </>
  );
}

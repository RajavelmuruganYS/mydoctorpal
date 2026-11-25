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
import { radiologyApi } from "../../api/radiologyApi";
import { toArray } from "./radiologyData";
import { formatDateTime } from "../../utils/formatDate";
import { STUDY_STATUSES } from "../../utils/constants";
import SearchBar from "../../components/common/SearchBar";

const viewerHref = (study) => {
  if (!study.viewerUrl) return "";
  try {
    const url = new globalThis.URL(study.viewerUrl, window.location.origin);
    if (url.hostname === "localhost" && url.port === "3000" && url.pathname.startsWith("/demo/radiology/")) {
      return `/app/radiology/viewer/${study.id}`;
    }
  } catch {
    return "";
  }
  return study.viewerUrl;
};

export default function RadiologyStudies() {
  const [status, setStatus] = useState("AVAILABLE");
  const [accession, setAccession] = useState("");
  const term = accession.trim();
  const state = useApi(() => (term ? radiologyApi.byAccession(term) : radiologyApi.studiesByStatus(status)), [status, term]);

  return (
    <>
      <BackLink to="/app/radiology">Radiology</BackLink>
      <PageHeader eyebrow="Radiology" title="Imaging studies" description="Browse studies by status, or look one up by accession number." />
      <div className="surface p-5">
        <div className="mb-4 flex flex-wrap items-center gap-4">
          <div className="w-full max-w-xs">
            <SearchBar value={accession} onChange={setAccession} placeholder="Accession number…" />
          </div>
          {!term && <Segmented options={STUDY_STATUSES} value={status} onChange={setStatus} />}
        </div>
        <Async state={state}>
          {(data) => (
            <DataTable
              rows={toArray(data)}
              empty={<EmptyState title="No studies found" message={term ? "No study has this accession number." : "No studies with this status."} />}
              columns={[
                { key: "accessionNumber", label: "Accession" },
                { key: "radiologyOrderId", label: "Order", render: (s) => <Link className="font-bold underline" to={`/app/radiology/orders/${s.radiologyOrderId}`}>#{s.radiologyOrderId}</Link> },
                { key: "imageCount", label: "Images" },
                { key: "storageType", label: "Storage" },
                { key: "performedAt", label: "Performed", render: (s) => formatDateTime(s.performedAt) },
                { key: "studyStatus", label: "Status", render: (s) => <StatusBadge status={s.studyStatus} /> },
                { key: "viewerUrl", label: "Viewer", render: (s) => (viewerHref(s) ? <a href={viewerHref(s)} target="_blank" rel="noreferrer" className="font-bold text-forest underline">Open</a> : "—") },
              ]}
            />
          )}
        </Async>
      </div>
    </>
  );
}

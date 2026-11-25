import { useState } from "react";
import { useParams } from "react-router-dom";
import PageHeader from "../../components/common/PageHeader";
import Async from "../../components/common/Async";
import DetailGrid from "../../components/common/DetailGrid";
import StatusBadge from "../../components/common/StatusBadge";
import Button from "../../components/common/Button";
import Modal from "../../components/common/Modal";
import Input from "../../components/common/Input";
import Select from "../../components/common/Select";
import Textarea from "../../components/common/Textarea";
import EmptyState from "../../components/common/EmptyState";
import ConfirmDialog from "../../components/common/ConfirmDialog";
import { BackLink } from "../../components/common/RecordStates";
import { useApi } from "../../hooks/useApi";
import { useForm } from "../../hooks/useForm";
import { useMutation } from "../../hooks/useMutation";
import { useDirectory } from "../../hooks/useDirectory";
import { useAuth } from "../../context/AuthContext";
import { radiologyApi } from "../../api/radiologyApi";
import { compact, emptyOn404, toInt } from "../../utils/payload";
import { formatDateTime } from "../../utils/formatDate";
import { STORAGE_TYPES } from "../../utils/constants";
import { toArray } from "./radiologyData";

const viewerHref = (study) => {
  return study?.id ? `/app/radiology/viewer/${study.id}` : "";
};

// Mirrors the transitions enforced by the backend.
const NEXT = {
  ORDERED: ["SCHEDULED", "CANCELLED"],
  SCHEDULED: ["ARRIVED", "CANCELLED"],
  ARRIVED: ["IN_PROGRESS", "CANCELLED"],
  IN_PROGRESS: ["COMPLETED"],
  COMPLETED: [],
  CANCELLED: [],
};

function StudyModal({ orderId, open, onClose, onSaved }) {
  const blank = { accessionNumber: "", studyInstanceUid: "", seriesCount: "", imageCount: "", storageType: "PACS", storageReference: "", viewerUrl: "" };
  const form = useForm(blank);
  const save = useMutation(radiologyApi.createStudy, { success: "Study registered." });
  const submit = async (event) => {
    event.preventDefault();
    const v = form.values;
    const body = compact({ ...v, radiologyOrderId: Number(orderId), seriesCount: toInt(v.seriesCount), imageCount: toInt(v.imageCount) });
    if (await save.run(body)) {
      form.setValues(blank);
      onSaved();
    }
  };
  return (
    <Modal open={open} onClose={onClose} title="Register imaging study">
      <form onSubmit={submit} className="space-y-4">
        <Input label="Accession number" required maxLength={100} {...form.bind("accessionNumber", save.fieldErrors)} />
        <Input label="Study instance UID" maxLength={255} {...form.bind("studyInstanceUid", save.fieldErrors)} />
        <div className="grid gap-4 sm:grid-cols-3">
          <Input label="Series" type="number" min={0} {...form.bind("seriesCount", save.fieldErrors)} />
          <Input label="Images" type="number" min={0} {...form.bind("imageCount", save.fieldErrors)} />
          <Select label="Storage" {...form.bind("storageType", save.fieldErrors)}>
            {STORAGE_TYPES.map((s) => <option key={s}>{s}</option>)}
          </Select>
        </div>
        <Input label="Storage reference" maxLength={1000} {...form.bind("storageReference", save.fieldErrors)} />
        <Input label="Viewer URL" type="url" maxLength={2000} {...form.bind("viewerUrl", save.fieldErrors)} />
        <div className="flex justify-end gap-3">
          <Button variant="secondary" onClick={onClose}>Cancel</Button>
          <Button type="submit" loading={save.loading}>Register study</Button>
        </div>
      </form>
    </Modal>
  );
}

function ReportModal({ orderId, open, onClose, onSaved, existing }) {
  const amending = Boolean(existing);
  const blank = { reportText: "", findings: "", impression: "", criticalFinding: false, criticalFindingNotes: "", priorStudyReference: "" };
  const form = useForm(existing ? { ...blank, ...Object.fromEntries(Object.entries(existing).filter(([k]) => k in blank).map(([k, v]) => [k, v ?? blank[k]])) } : blank);
  const save = useMutation(
    (body) => (amending ? radiologyApi.amendReport(existing.id, body) : radiologyApi.createReport({ ...body, radiologyOrderId: Number(orderId) })),
    { success: amending ? "Report amended." : "Report saved as draft." },
  );
  const submit = async (event) => {
    event.preventDefault();
    const v = form.values;
    const body = compact({ ...v, criticalFinding: Boolean(v.criticalFinding) });
    if (amending) delete body.priorStudyReference;
    if (await save.run(body)) onSaved();
  };
  return (
    <Modal open={open} onClose={onClose} title={amending ? "Amend report" : "Write report"}>
      <form onSubmit={submit} className="space-y-4">
        <Textarea label="Report" required maxLength={20000} {...form.bind("reportText", save.fieldErrors)} />
        <Textarea label="Findings" maxLength={20000} {...form.bind("findings", save.fieldErrors)} />
        <Textarea label="Impression" maxLength={20000} {...form.bind("impression", save.fieldErrors)} />
        <label className="flex items-center gap-2 text-sm font-semibold">
          <input type="checkbox" checked={Boolean(form.values.criticalFinding)} onChange={(e) => form.set("criticalFinding", e.target.checked)} />
          Critical finding
        </label>
        {form.values.criticalFinding && <Textarea label="Critical finding notes" maxLength={10000} {...form.bind("criticalFindingNotes", save.fieldErrors)} />}
        {!amending && <Input label="Prior study reference" maxLength={500} {...form.bind("priorStudyReference", save.fieldErrors)} />}
        <div className="flex justify-end gap-3">
          <Button variant="secondary" onClick={onClose}>Cancel</Button>
          <Button type="submit" loading={save.loading}>{amending ? "Save amendment" : "Save report"}</Button>
        </div>
      </form>
    </Modal>
  );
}

export default function RadiologyOrderDetails() {
  const { id } = useParams();
  const { hasRole, isPatient, userId } = useAuth();
  const dir = useDirectory();
  const order = useApi(() => radiologyApi.order(id), [id]);
  const studies = useApi(() => emptyOn404(radiologyApi.studiesByOrder(id)), [id]);
  const reports = useApi(() => emptyOn404(radiologyApi.reportByOrder(id)), [id]);
  const [next, setNext] = useState("");
  const [studyOpen, setStudyOpen] = useState(false);
  const [reportOpen, setReportOpen] = useState(false);
  const [amend, setAmend] = useState(null);
  const [finalizeId, setFinalizeId] = useState(null);
  const setStatus = useMutation((s) => radiologyApi.updateOrderStatus(id, s), { success: "Order status updated." });
  const finalize = useMutation((rid) => radiologyApi.finalizeReport(rid), { success: "Report finalized." });

  const applyStatus = async () => {
    if (next && (await setStatus.run(next))) {
      setNext("");
      order.reload();
    }
  };
  const doFinalize = async () => {
    if (await finalize.run(finalizeId)) {
      setFinalizeId(null);
      reports.reload();
    }
  };

  return (
    <>
      <BackLink to="/app/radiology/orders">Imaging orders</BackLink>
      <Async state={order}>
        {(o) => (
          <>
            <PageHeader eyebrow="Imaging order" title={`${o.modality} · order #${o.id}`} actions={<><StatusBadge status={o.urgency} /><StatusBadge status={o.status} /></>} />
            <div className="surface p-6">
              <DetailGrid
                items={[
                  { label: "Patient", value: isPatient ? "You" : (o.patientName || dir.patientName(o.patientId)) },
                  { label: "Ordering doctor", value: o.orderingDoctorName || dir.doctorName(o.orderingDoctorId) },
                  { label: "Ordered", value: formatDateTime(o.createdAt) },
                  { label: "Scheduled", value: formatDateTime(o.scheduledAt) },
                  { label: "Arrived", value: formatDateTime(o.arrivedAt) },
                  { label: "Study started", value: formatDateTime(o.studyStartedAt) },
                  { label: "Clinical indication", value: o.clinicalIndication },
                  { label: "Clinical history", value: o.clinicalHistory },
                ]}
              />
              {hasRole("ADMIN", "DOCTOR", "NURSE", "LAB_TECHNICIAN", "RADIOLOGY_TECHNICIAN") && (NEXT[o.status] || []).length > 0 && (
                <div className="mt-5 flex flex-wrap items-end gap-4 border-t border-forest/10 pt-5">
                  <Select label="Move order to" value={next} onChange={(e) => setNext(e.target.value)} className="w-56">
                    <option value="">Select…</option>
                    {NEXT[o.status].map((s) => <option key={s} value={s}>{s.replaceAll("_", " ")}</option>)}
                  </Select>
                  <Button onClick={applyStatus} loading={setStatus.loading} disabled={!next}>Update status</Button>
                </div>
              )}
            </div>

            <div className="surface mt-6 p-6">
              <div className="mb-4 flex items-center justify-between">
                <h3 className="font-bold">Imaging studies</h3>
                {hasRole("ADMIN", "DOCTOR", "NURSE", "LAB_TECHNICIAN", "RADIOLOGY_TECHNICIAN") && <Button variant="secondary" onClick={() => setStudyOpen(true)}>Register study</Button>}
              </div>
              <Async state={studies} inline>
                {(data) =>
                  toArray(data).length ? (
                    <div className="space-y-3">
                      {toArray(data).map((s) => (
                        <div key={s.id} className="rounded-2xl border border-forest/10 p-4">
                          <div className="mb-3 flex items-center justify-between">
                            <p className="font-bold">Accession {s.accessionNumber}</p>
                            <StatusBadge status={s.studyStatus} />
                          </div>
                          <DetailGrid
                            columns="sm:grid-cols-4"
                            items={[
                              { label: "Series", value: s.seriesCount },
                              { label: "Images", value: s.imageCount },
                              { label: "Storage", value: s.storageType },
                              { label: "Performed", value: formatDateTime(s.performedAt) },
                            ]}
                          />
                          {viewerHref(s) && (
                            <a href={viewerHref(s)} target="_blank" rel="noreferrer" className="mt-3 inline-block text-sm font-bold text-forest underline">
                              Open in viewer
                            </a>
                          )}
                        </div>
                      ))}
                    </div>
                  ) : (
                    <EmptyState title="No study registered" message="Images have not been registered for this order yet." />
                  )
                }
              </Async>
            </div>

            <div className="surface mt-6 p-6">
              <div className="mb-4 flex items-center justify-between">
                <h3 className="font-bold">Radiology report</h3>
                {hasRole("RADIOLOGY_TECHNICIAN") && toArray(reports.data).length === 0 && <Button variant="secondary" onClick={() => setReportOpen(true)}>Write report</Button>}
              </div>
              <Async state={reports} inline>
                {(data) =>
                  toArray(data).length ? (
                    <div className="space-y-4">
                      {toArray(data).map((r) => (
                        <div key={r.id} className="rounded-2xl border border-forest/10 p-4">
                          <div className="mb-3 flex flex-wrap items-center justify-between gap-3">
                            <div className="flex gap-2">
                              <StatusBadge status={r.status} />
                              {r.criticalFinding && <StatusBadge status="CRITICAL" />}
                            </div>
                            {hasRole("RADIOLOGY_TECHNICIAN") && r.reportedByUserId === userId && (
                              <div className="flex gap-2">
                                {r.status === "DRAFT" && <Button onClick={() => setFinalizeId(r.id)}>Finalize</Button>}
                                {r.status === "FINAL" && <Button variant="secondary" onClick={() => setAmend(r)}>Amend</Button>}
                              </div>
                            )}
                          </div>
                          <DetailGrid
                            columns="sm:grid-cols-1"
                            items={[
                              { label: "Report", value: <span className="whitespace-pre-wrap">{r.reportText}</span> },
                              { label: "Findings", value: <span className="whitespace-pre-wrap">{r.findings}</span> },
                              { label: "Impression", value: <span className="whitespace-pre-wrap">{r.impression}</span> },
                              ...(r.criticalFinding ? [{ label: "Critical finding notes", value: r.criticalFindingNotes }] : []),
                              { label: "Signed", value: formatDateTime(r.signedAt) },
                            ]}
                          />
                        </div>
                      ))}
                    </div>
                  ) : (
                    <EmptyState title="No report yet" message="A radiologist has not written a report for this order." />
                  )
                }
              </Async>
            </div>

            <StudyModal orderId={id} open={studyOpen} onClose={() => setStudyOpen(false)} onSaved={() => { setStudyOpen(false); studies.reload(); }} />
            {reportOpen && <ReportModal orderId={id} open onClose={() => setReportOpen(false)} onSaved={() => { setReportOpen(false); reports.reload(); }} />}
            {amend && <ReportModal orderId={id} open existing={amend} onClose={() => setAmend(null)} onSaved={() => { setAmend(null); reports.reload(); }} />}
            <ConfirmDialog
              open={Boolean(finalizeId)}
              loading={finalize.loading}
              title="Finalize this report?"
              message="A finalized report is signed and can only be changed by an amendment."
              confirmLabel="Finalize"
              onClose={() => setFinalizeId(null)}
              onConfirm={doFinalize}
            />
          </>
        )}
      </Async>
    </>
  );
}

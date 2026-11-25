import { useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import Async from "../../components/common/Async";
import DetailGrid from "../../components/common/DetailGrid";
import PageHeader from "../../components/common/PageHeader";
import StatusBadge from "../../components/common/StatusBadge";
import Button from "../../components/common/Button";
import EmptyState from "../../components/common/EmptyState";
import { BackLink } from "../../components/common/RecordStates";
import { useApi } from "../../hooks/useApi";
import { useMutation } from "../../hooks/useMutation";
import { radiologyApi } from "../../api/radiologyApi";
import { formatDateTime } from "../../utils/formatDate";
import { asList } from "../../utils/payload";

const isImage = (contentType = "") => contentType.startsWith("image/");
const isPdf = (contentType = "") => contentType === "application/pdf";

export default function RadiologyViewer() {
  const { studyId } = useParams();
  const study = useApi(() => radiologyApi.study(studyId), [studyId]);
  const order = useApi(
    () => study.data?.radiologyOrderId ? radiologyApi.order(study.data.radiologyOrderId) : Promise.resolve({ data: null }),
    [study.data?.radiologyOrderId],
    Boolean(study.data?.radiologyOrderId),
  );
  const files = useApi(() => radiologyApi.studyFiles(studyId), [studyId], Boolean(studyId));
  const [selectedFileId, setSelectedFileId] = useState(null);
  const [previewUrl, setPreviewUrl] = useState("");
  const upload = useMutation((payload) => radiologyApi.uploadStudyFile(payload.studyId, payload.file), { success: "File uploaded." });
  const download = useMutation((payload) => radiologyApi.downloadStudyFile(payload.studyId, payload.fileId), { success: "Download started." });

  const list = useMemo(() => asList(files.data), [files.data]);
  const selectedFile = useMemo(
    () => list.find((file) => file.id === Number(selectedFileId)) || list[0] || null,
    [list, selectedFileId],
  );

  useEffect(() => {
    setSelectedFileId((current) => {
      if (list.length === 0) return null;
      if (!current && list[0]) return list[0].id;
      return list.some((file) => file.id === current) ? current : list[0].id;
    });
  }, [list]);

  useEffect(() => {
    if (!selectedFile || !studyId) {
      setPreviewUrl("");
      return;
    }

    let cancelled = false;
    let objectUrl = "";
    radiologyApi.downloadStudyFile(studyId, selectedFile.id)
      .then((response) => {
        if (cancelled) return;
        const blob = new Blob([response.data], { type: selectedFile.contentType || "application/octet-stream" });
        objectUrl = URL.createObjectURL(blob);
        setPreviewUrl(objectUrl);
      })
      .catch(() => setPreviewUrl(""));

    return () => {
      cancelled = true;
      if (objectUrl) URL.revokeObjectURL(objectUrl);
    };
  }, [selectedFile, studyId]);

  const handleUpload = async (event) => {
    const file = event.target.files?.[0];
    if (!file || !studyId) return;
    const result = await upload.run({ studyId: Number(studyId), file });
    if (result) {
      event.target.value = "";
      files.reload();
    }
  };

  const handleDownload = async (file) => {
    const result = await download.run({ studyId: Number(studyId), fileId: file.id });
    if (!result) return;
    const blob = new Blob([result.data], { type: file.contentType || "application/octet-stream" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = file.originalFileName || "study-file";
    link.click();
    URL.revokeObjectURL(url);
  };

  return (
    <>
      <BackLink to={study.data?.radiologyOrderId ? `/app/radiology/orders/${study.data.radiologyOrderId}` : "/app/radiology"}>Back to imaging order</BackLink>
      <Async state={study}>
        {(s) => (
          <>
            <PageHeader
              eyebrow="Imaging study"
              title={s.accessionNumber || `Study #${s.id}`}
              description="Study files and metadata for the current imaging study."
              actions={<StatusBadge status={s.studyStatus} />}
            />
            <div className="grid gap-6 lg:grid-cols-[minmax(0,1fr)_22rem]">
              <section className="surface overflow-hidden p-6">
                {selectedFile && previewUrl ? (
                  <>
                    {isImage(selectedFile.contentType) ? (
                      <img src={previewUrl} alt={selectedFile.originalFileName} className="aspect-[4/3] w-full rounded-xl border border-forest/10 object-contain bg-forest/[0.03]" />
                    ) : isPdf(selectedFile.contentType) ? (
                      <iframe title={selectedFile.originalFileName} src={previewUrl} className="aspect-[4/3] w-full rounded-xl border border-forest/10 bg-white" />
                    ) : (
                      <div className="flex aspect-[4/3] items-center justify-center rounded-xl border border-dashed border-forest/20 bg-forest/[0.03] p-8 text-center">
                        <div>
                          <p className="font-display text-2xl font-extrabold text-forest">Study file preview</p>
                          <p className="mt-2 max-w-md text-sm muted">{selectedFile.originalFileName}</p>
                        </div>
                      </div>
                    )}
                    <div className="mt-4 flex flex-wrap items-center gap-3">
                      <Button onClick={() => handleDownload(selectedFile)} loading={download.loading}>Download</Button>
                      <span className="text-sm muted">{selectedFile.originalFileName}</span>
                    </div>
                  </>
                ) : (
                  <div className="flex aspect-[4/3] min-h-80 items-center justify-center rounded-xl border border-dashed border-forest/20 bg-forest/[0.03] p-8 text-center">
                    <div>
                      <p className="font-display text-2xl font-extrabold text-forest">No study files uploaded</p>
                      <p className="mt-2 max-w-md text-sm muted">Upload JPG, PNG, PDF, or TXT files to attach them to this study.</p>
                    </div>
                  </div>
                )}
                <div className="mt-5 grid gap-3 sm:grid-cols-3">
                  {[
                    ["Series", s.seriesCount ?? "—"],
                    ["Images", s.imageCount ?? "—"],
                    ["Storage", s.storageType || "—"],
                  ].map(([label, value]) => (
                    <div key={label} className="rounded-xl border border-forest/10 p-4">
                      <p className="text-xs font-bold uppercase tracking-wider text-forest/50">{label}</p>
                      <p className="mt-1 text-lg font-bold">{value}</p>
                    </div>
                  ))}
                </div>
              </section>

              <section className="surface p-6">
                <h2 className="mb-4 font-bold">Study details</h2>
                <Async state={order} inline>
                  {(o) => (
                    <DetailGrid
                      columns="sm:grid-cols-1"
                      items={[
                        { label: "Accession", value: s.accessionNumber },
                        { label: "Study UID", value: s.studyInstanceUid },
                        { label: "Modality", value: o?.modality },
                        { label: "Order status", value: o?.status ? <StatusBadge status={o.status} /> : null },
                        { label: "Performed", value: formatDateTime(s.performedAt) },
                        { label: "Storage reference", value: s.storageReference },
                      ]}
                    />
                  )}
                </Async>

                <div className="mt-6 border-t border-forest/10 pt-4">
                  <label className="block text-sm font-semibold text-ink">
                    Upload study file
                    <input type="file" accept="image/*,.pdf,.txt" className="mt-2 block w-full text-sm" onChange={handleUpload} />
                  </label>
                </div>

                <div className="mt-6">
                  <h3 className="mb-3 font-bold">Files</h3>
                  <Async state={files} inline>
                    {(data) => {
                      const fileList = asList(data);
                      if (!fileList.length) return <EmptyState title="No files" message="No files have been uploaded for this study yet." />;
                      return (
                        <div className="space-y-2">
                          {fileList.map((file) => (
                            <button
                              key={file.id}
                              type="button"
                              onClick={() => setSelectedFileId(file.id)}
                              className={`flex w-full items-center justify-between rounded-xl border px-3 py-2 text-left ${selectedFile?.id === file.id ? "border-forest bg-forest/5" : "border-forest/10"}`}
                            >
                              <span className="font-medium">{file.originalFileName}</span>
                              <span className="text-xs muted">{file.contentType}</span>
                            </button>
                          ))}
                        </div>
                      );
                    }}
                  </Async>
                </div>
              </section>
            </div>
          </>
        )}
      </Async>
    </>
  );
}
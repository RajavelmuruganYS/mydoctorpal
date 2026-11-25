import { useParams } from "react-router-dom";
import PageHeader from "../../components/common/PageHeader";
import Async from "../../components/common/Async";
import DetailGrid from "../../components/common/DetailGrid";
import StatusBadge from "../../components/common/StatusBadge";
import Input from "../../components/common/Input";
import Textarea from "../../components/common/Textarea";
import Button from "../../components/common/Button";
import EmptyState from "../../components/common/EmptyState";
import { BackLink } from "../../components/common/RecordStates";
import { useApi } from "../../hooks/useApi";
import { useForm } from "../../hooks/useForm";
import { useMutation } from "../../hooks/useMutation";
import { useDirectory } from "../../hooks/useDirectory";
import { useAuth } from "../../context/AuthContext";
import { laboratoryApi } from "../../api/laboratoryApi";
import { asList, compact, emptyOn404 } from "../../utils/payload";
import { formatDateTime } from "../../utils/formatDate";

function ResultForm({ orderId, onSaved }) {
  const form = useForm({ resultValue: "", unit: "", referenceRange: "", interpretation: "" });
  const save = useMutation((body) => laboratoryApi.addResult(orderId, body), { success: "Result recorded." });
  const submit = async (event) => {
    event.preventDefault();
    if (await save.run(compact(form.values))) {
      form.setValues({ resultValue: "", unit: "", referenceRange: "", interpretation: "" });
      onSaved();
    }
  };
  return (
    <form onSubmit={submit} className="surface mt-6 space-y-4 p-6">
      <h3 className="font-bold">Record a result</h3>
      <div className="grid gap-4 md:grid-cols-3">
        <Input label="Result value" required {...form.bind("resultValue", save.fieldErrors)} />
        <Input label="Unit" maxLength={50} {...form.bind("unit", save.fieldErrors)} />
        <Input label="Reference range" maxLength={200} {...form.bind("referenceRange", save.fieldErrors)} />
      </div>
      <Textarea label="Interpretation" maxLength={5000} {...form.bind("interpretation", save.fieldErrors)} />
      <div className="flex justify-end">
        <Button type="submit" loading={save.loading}>Save result</Button>
      </div>
    </form>
  );
}

export default function LabOrderDetails() {
  const { id } = useParams();
  const { hasRole, isPatient } = useAuth();
  const dir = useDirectory();
  const order = useApi(() => laboratoryApi.order(id), [id]);
  const results = useApi(() => emptyOn404(laboratoryApi.orderResults(id)), [id]);

  const reloadAll = () => {
    order.reload();
    results.reload();
  };

  return (
    <>
      <BackLink to="/app/laboratory/orders">Lab orders</BackLink>
      <Async state={order}>
        {(o) => (
          <>
            <PageHeader eyebrow="Lab order" title={o.testName} description={o.testCode ? `Code ${o.testCode}` : undefined} actions={<><StatusBadge status={o.priority} /><StatusBadge status={o.status} /></>} />
            <div className="surface p-6">
              <DetailGrid
                items={[
                  { label: "Patient", value: isPatient ? "You" : dir.patientName(o.patientId) },
                  { label: "Ordered by", value: dir.doctorName(o.doctorId) },
                  { label: "Ordered at", value: formatDateTime(o.orderedAt) },
                  { label: "Clinical notes", value: o.clinicalNotes },
                ]}
              />
            </div>
            <div className="surface mt-6 p-6">
              <h3 className="mb-4 font-bold">Results</h3>
              <Async state={results} inline>
                {(data) =>
                  asList(data).length ? (
                    <div className="space-y-4">
                      {asList(data).map((r) => (
                        <div key={r.id} className="rounded-2xl border border-forest/10 p-4">
                          <div className="mb-3 flex items-center justify-between">
                            <p className="font-display text-2xl font-extrabold">
                              {r.resultValue} <span className="text-base font-semibold muted">{r.unit}</span>
                            </p>
                            <StatusBadge status={r.resultStatus} />
                          </div>
                          <DetailGrid
                            columns="sm:grid-cols-3"
                            items={[
                              { label: "Reference range", value: r.referenceRange },
                              { label: "Performed", value: formatDateTime(r.performedAt) },
                              { label: "Verified", value: formatDateTime(r.verifiedAt) },
                              { label: "Interpretation", value: r.interpretation },
                            ]}
                          />
                        </div>
                      ))}
                    </div>
                  ) : (
                    <EmptyState title="No result yet" message="The laboratory has not recorded a result for this test." />
                  )
                }
              </Async>
            </div>
            {hasRole("LAB_TECHNICIAN") && <ResultForm orderId={id} onSaved={reloadAll} />}
          </>
        )}
      </Async>
    </>
  );
}

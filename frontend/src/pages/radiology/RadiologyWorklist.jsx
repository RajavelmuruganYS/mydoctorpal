import { useState } from "react";
import { Link } from "react-router-dom";
import PageHeader from "../../components/common/PageHeader";
import Async from "../../components/common/Async";
import DataTable from "../../components/common/DataTable";
import EmptyState from "../../components/common/EmptyState";
import StatusBadge from "../../components/common/StatusBadge";
import Button from "../../components/common/Button";
import Select from "../../components/common/Select";
import { BackLink } from "../../components/common/RecordStates";
import { useApi } from "../../hooks/useApi";
import { useMutation } from "../../hooks/useMutation";
import { useDirectory } from "../../hooks/useDirectory";
import { radiologyApi } from "../../api/radiologyApi";
import { formatDateTime } from "../../utils/formatDate";
import { RADIOLOGY_ORDER_STATUSES } from "../../utils/constants";
import { asList } from "../../utils/payload";

const NEXT = {
  ORDERED: ["SCHEDULED", "CANCELLED"],
  SCHEDULED: ["ARRIVED", "CANCELLED"],
  ARRIVED: ["IN_PROGRESS", "CANCELLED"],
  IN_PROGRESS: ["COMPLETED"],
  COMPLETED: [],
  CANCELLED: [],
};

export default function RadiologyWorklist() {
  const dir = useDirectory();
  const [status, setStatus] = useState("ORDERED");
  const orders = useApi(() => radiologyApi.ordersByStatus(status), [status]);
  const [nextMap, setNextMap] = useState({});
  const update = useMutation((payload) => radiologyApi.updateOrderStatus(payload.id, payload.status), { success: "Order status updated." });

  const applyStatus = async (orderId, value) => {
    if (!value) return;
    const ok = await update.run({ id: orderId, status: value });
    if (ok) {
      setNextMap((current) => ({ ...current, [orderId]: "" }));
      orders.reload();
    }
  };

  return (
    <>
      <BackLink to="/app/radiology">Radiology</BackLink>
      <PageHeader
        eyebrow="Radiology"
        title="Technician worklist"
        description="Move imaging orders through intake, prep, and completion stages."
      />
      <div className="surface p-5">
        <div className="mb-4 flex flex-wrap items-center gap-4">
          <Select label="Status" className="w-52" value={status} onChange={(e) => setStatus(e.target.value)}>
            {RADIOLOGY_ORDER_STATUSES.map((s) => <option key={s} value={s}>{s.replaceAll("_", " ")}</option>)}
          </Select>
        </div>
        <Async state={orders}>
          {(data) => {
            const list = asList(data);
            return list.length ? (
              <DataTable
                rows={list}
                empty={<EmptyState title="No work items" message="No orders in this queue." />}
                columns={[
                  { key: "id", label: "Order", render: (o) => <Link className="font-bold underline" to={`/app/radiology/orders/${o.id}`}>#{o.id}</Link> },
                  { key: "modality", label: "Modality" },
                  { key: "patientId", label: "Patient", render: (o) => o.patientName || dir.patientName(o.patientId) },
                  { key: "urgency", label: "Urgency", render: (o) => <StatusBadge status={o.urgency} /> },
                  { key: "createdAt", label: "Ordered", render: (o) => formatDateTime(o.createdAt) },
                  { key: "status", label: "Status", render: (o) => <StatusBadge status={o.status} /> },
                  { key: "actions", label: "Action", render: (o) => (
                    <div className="flex items-center gap-2">
                      <Select
                        value={nextMap[o.id] ?? ""}
                        onChange={(e) => setNextMap((current) => ({ ...current, [o.id]: e.target.value }))}
                        className="w-36"
                      >
                        <option value="">Choose…</option>
                        {NEXT[o.status]?.map((value) => <option key={value} value={value}>{value.replaceAll("_", " ")}</option>)}
                      </Select>
                      <Button
                        size="sm"
                        variant="secondary"
                        onClick={() => applyStatus(o.id, nextMap[o.id] ?? "")}
                        disabled={!nextMap[o.id] || update.loading}
                        loading={update.loading && nextMap[o.id] !== undefined}
                      >
                        Update
                      </Button>
                    </div>
                  ) },
                ]}
              />
            ) : (
              <EmptyState title="No work items" message="No orders in this queue." />
            );
          }}
        </Async>
      </div>
    </>
  );
}

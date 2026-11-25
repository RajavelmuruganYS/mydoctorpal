import { Link } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import StatusBadge from "../../components/common/StatusBadge";
import { pharmacyApi } from "../../api/pharmacyApi";
import { prescriptionApi } from "../../api/prescriptionApi";
import { asList } from "../../utils/payload";
import { formatDateTime } from "../../utils/formatDate";
import { DashboardShell, MiniTable, Panel, count, useDashboardData } from "./dashboardKit";

export default function PharmacyDashboard() {
  const { userId } = useAuth();
  const data = useDashboardData(() => ({
    mine: () => pharmacyApi.byPharmacist(userId),
    drugs: () => prescriptionApi.drugs(),
  }), [userId]);
  const d = data.data || {};

  return (
    <DashboardShell
      title="Pharmacy desk"
      description="Dispense signed prescriptions and manage the drug catalogue."
      data={data}
      stats={[
        { label: "Dispensed by me", value: count(d.mine), to: "/app/pharmacy" },
        { label: "Active drugs", value: d.drugs ? asList(d.drugs).filter((x) => x.active).length : null, to: "/app/drugs" },
      ]}
    >
      <div className="mb-6 flex flex-wrap gap-3">
        <Link to="/app/pharmacy" className="btn-primary">Find a prescription to dispense</Link>
        <Link to="/app/drugs" className="btn-secondary">Drug catalogue</Link>
      </div>
      <Panel title="My recent dispensations" to="/app/pharmacy">
        <MiniTable
          rows={asList(d.mine).sort((a, b) => String(b.dispensedAt).localeCompare(String(a.dispensedAt)))}
          rowLink={(x) => `/app/pharmacy/dispensations/${x.id}`}
          empty="Nothing dispensed yet"
          columns={[
            { key: "id", label: "Dispensation", render: (x) => `#${x.id}` },
            { key: "prescriptionId", label: "Prescription", render: (x) => `#${x.prescriptionId}` },
            { key: "dispensedAt", label: "Dispensed", render: (x) => formatDateTime(x.dispensedAt) },
            { key: "status", label: "Status", render: (x) => <StatusBadge status={x.status} /> },
          ]}
        />
      </Panel>
    </DashboardShell>
  );
}

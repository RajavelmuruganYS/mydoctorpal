import { Link } from "react-router-dom";
import { dashboardApi } from "../../api/dashboardApi";
import { useApi } from "../../hooks/useApi";
import Async from "../../components/common/Async";
import PageHeader from "../../components/common/PageHeader";
import { StatGrid } from "./dashboardKit";

const label = (key) => key.replace(/([A-Z])/g, " $1").replace(/^./, (c) => c.toUpperCase());
const LINKS = { totalUsers: "/app/admin/users", totalPatients: "/app/patients", todayAppointments: "/app/appointments", activeAdmissions: "/app/inpatient", pendingLabOrders: "/app/laboratory/orders", pendingRadiologyOrders: "/app/radiology/orders", pendingPrescriptions: "/app/prescriptions" };

export default function AdminDashboard() {
  const state = useApi(() => dashboardApi.admin(), []);
  return (
    <>
      <PageHeader eyebrow="Dashboard" title="Hospital overview" description="Live counts from the hospital database." />
      <Async state={state}>
        {(data) => (
          <>
            <StatGrid
              stats={Object.entries(data?.statistics || {})
                .filter(([, v]) => typeof v === "number")
                .map(([key, value]) => ({ label: label(key), value, to: LINKS[key] }))}
            />
            <div className="flex flex-wrap gap-3">
              <Link to="/app/admin" className="btn-secondary">System overview</Link>
              <Link to="/app/admin/users" className="btn-secondary">Manage users</Link>
              <Link to="/app/admin/audit-logs" className="btn-secondary">Audit logs</Link>
            </div>
          </>
        )}
      </Async>
    </>
  );
}

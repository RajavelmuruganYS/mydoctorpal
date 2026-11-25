import { Link } from "react-router-dom";
import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import PageHeader from "../../components/common/PageHeader";
import Async from "../../components/common/Async";
import { useApi } from "../../hooks/useApi";
import { dashboardApi } from "../../api/dashboardApi";

const label = (key) => key.replace(/([A-Z])/g, " $1").replace(/^./, (c) => c.toUpperCase());

export default function AdminSystemOverview() {
  const state = useApi(() => dashboardApi.admin(), []);
  return (
    <>
      <PageHeader eyebrow="Administration" title="System overview" description="Live counts from the hospital database." />
      <Async state={state}>
        {(data) => {
          const stats = data?.statistics || {};
          const entries = Object.entries(stats).filter(([, v]) => typeof v === "number");
          return (
            <>
              <div className="mb-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
                {entries.map(([key, value]) => (
                  <div key={key} className="surface p-5">
                    <p className="text-xs font-bold uppercase tracking-wider text-forest/50">{label(key)}</p>
                    <p className="mt-2 font-display text-4xl font-extrabold">{value}</p>
                  </div>
                ))}
              </div>
              <div className="surface mb-6 p-5">
                <h3 className="mb-4 font-bold">At a glance</h3>
                <div className="h-72">
                  <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={entries.map(([key, value]) => ({ name: label(key), value }))}>
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis dataKey="name" tick={{ fontSize: 11 }} interval={0} angle={-15} height={60} textAnchor="end" />
                      <YAxis allowDecimals={false} />
                      <Tooltip />
                      <Bar dataKey="value" fill="#0b4b3e" radius={[8, 8, 0, 0]} />
                    </BarChart>
                  </ResponsiveContainer>
                </div>
              </div>
              <div className="flex flex-wrap gap-3">
                <Link to="/app/admin/users" className="btn-secondary">Manage users</Link>
                <Link to="/app/admin/audit-logs" className="btn-secondary">View audit logs</Link>
                <Link to="/app/patients/new" className="btn-secondary">Create patient record</Link>
              </div>
            </>
          );
        }}
      </Async>
    </>
  );
}

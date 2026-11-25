import { Link } from "react-router-dom";
import { useApi } from "../../hooks/useApi";
import PageHeader from "../../components/common/PageHeader";
import DataTable from "../../components/common/DataTable";
import EmptyState from "../../components/common/EmptyState";
import { asList } from "../../utils/payload";

/**
 * Run several independent API calls; a failed call yields `null` for that key
 * instead of breaking the whole dashboard.
 */
export const useDashboardData = (calls, deps, enabled = true) =>
  useApi(
    async () => {
      const entries = await Promise.all(
        Object.entries(calls()).map(async ([key, call]) => {
          try {
            return [key, (await call()).data];
          } catch {
            return [key, null];
          }
        }),
      );
      return { data: Object.fromEntries(entries) };
    },
    deps,
    enabled,
  );

export const count = (value) => (Array.isArray(value) ? value.length : null);

export function StatGrid({ stats, loading }) {
  return (
    <div className="mb-6 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
      {stats.map((s) => {
        const body = (
          <div className="surface h-full p-5 transition hover:shadow-soft">
            <p className="text-xs font-bold uppercase tracking-wider text-forest/50">{s.label}</p>
            <p className="mt-2 font-display text-4xl font-extrabold">{loading ? "…" : (s.value ?? "—")}</p>
            {s.hint && <p className="mt-1 text-xs muted">{s.hint}</p>}
          </div>
        );
        return s.to ? <Link key={s.label} to={s.to}>{body}</Link> : <div key={s.label}>{body}</div>;
      })}
    </div>
  );
}

export function Panel({ title, to, linkLabel = "View all", children }) {
  return (
    <div className="surface p-5">
      <div className="mb-4 flex items-center justify-between">
        <h3 className="font-bold">{title}</h3>
        {to && <Link to={to} className="text-xs font-bold text-forest hover:underline">{linkLabel} →</Link>}
      </div>
      {children}
    </div>
  );
}

export function MiniTable({ rows, columns, rowLink, empty = "Nothing to show." }) {
  return (
    <DataTable
      rows={asList(rows).slice(0, 6)}
      columns={columns}
      rowLink={rowLink}
      empty={<EmptyState title={empty} message="" />}
    />
  );
}

export function DashboardShell({ title, description, data, children, stats }) {
  const failed = data.data && Object.values(data.data).some((v) => v === null);
  return (
    <>
      <PageHeader eyebrow="Dashboard" title={title} description={description} />
      {failed && (
        <div className="mb-5 rounded-xl bg-amber-50 p-3 text-sm text-amber-900">
          Some figures could not be loaded and are shown as “—”.
        </div>
      )}
      <StatGrid stats={stats} loading={data.loading && !data.data} />
      {children}
    </>
  );
}

import { STATUS_COLORS } from "../../utils/constants";

export default function StatusBadge({ status }) {
  if (!status) return <span className="muted">—</span>;
  return (
    <span
      className={`inline-flex whitespace-nowrap rounded-full px-2.5 py-1 text-xs font-bold ${STATUS_COLORS[status] || "bg-sage text-forest"}`}
    >
      {String(status).replaceAll("_", " ")}
    </span>
  );
}

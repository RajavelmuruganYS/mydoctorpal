import { Link, useLocation } from "react-router-dom";
export default function Breadcrumbs() {
  const { pathname } = useLocation();
  const parts = pathname.split("/").filter(Boolean);
  return (
    <div className="mb-4 flex items-center gap-2 text-xs muted">
      <Link to="/app/dashboard" className="hover:text-forest">
        Workspace
      </Link>
      {parts.slice(1).map((p) => (
        <span key={p}>
          / <span className="capitalize">{p.replaceAll("-", " ")}</span>
        </span>
      ))}
    </div>
  );
}

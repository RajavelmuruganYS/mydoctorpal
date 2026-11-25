import { Link } from "react-router-dom";
export default function Unauthorized() {
  return (
    <div className="grid min-h-[70vh] place-items-center text-center">
      <div>
        <div className="text-7xl font-extrabold text-forest/15">403</div>
        <h1 className="mt-4 font-display text-3xl font-extrabold text-forest">
          Access restricted
        </h1>
        <p className="mt-2 muted">
          Your role does not have access to this workspace.
        </p>
        <Link to="/app/dashboard" className="btn-primary mt-6">
          Back to dashboard
        </Link>
      </div>
    </div>
  );
}

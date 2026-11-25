import { Link } from "react-router-dom";
export default function NotFound() {
  return (
    <div className="grid min-h-[70vh] place-items-center text-center">
      <div>
        <div className="text-7xl font-extrabold text-forest/15">404</div>
        <h1 className="mt-4 font-display text-3xl font-extrabold text-forest">
          Page not found
        </h1>
        <p className="mt-2 muted">The page you requested does not exist.</p>
        <Link to="/app/dashboard" className="btn-primary mt-6">
          Back to dashboard
        </Link>
      </div>
    </div>
  );
}

import { useState } from "react";
import { Link, useLocation, useNavigate, useSearchParams } from "react-router-dom";
import AuthShell from "./AuthShell";
import Input from "../../components/common/Input";
import Button from "../../components/common/Button";
import { useAuth } from "../../context/AuthContext";
import { getApiError } from "../../utils/errorHandler";

export default function Login() {
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [params] = useSearchParams();

  const submit = async (event) => {
    event.preventDefault();
    setLoading(true);
    setError("");
    try {
      await login({ email: form.email.trim(), password: form.password });
      const target = location.state?.from?.pathname;
      navigate(target && target.startsWith("/app") ? target : "/app/dashboard", { replace: true });
    } catch (err) {
      setError(getApiError(err));
    } finally {
      setLoading(false);
    }
  };

  const needsVerification = /verify your email/i.test(error);

  return (
    <AuthShell title="Good to see you again." subtitle="Sign in to continue your care journey.">
      <form onSubmit={submit} className="space-y-4">
        {params.get("expired") && (
          <div className="rounded-xl bg-amber-50 p-3 text-sm text-amber-800">
            Your session expired. Please sign in again.
          </div>
        )}
        <Input
          label="Email address"
          type="email"
          required
          autoComplete="username"
          value={form.email}
          onChange={(e) => setForm({ ...form, email: e.target.value })}
        />
        <Input
          label="Password"
          type="password"
          required
          autoComplete="current-password"
          value={form.password}
          onChange={(e) => setForm({ ...form, password: e.target.value })}
        />
        <div className="flex justify-end">
          <Link to="/forgot-password" className="text-xs font-bold text-forest">
            Forgot password?
          </Link>
        </div>
        {error && (
          <div className="rounded-xl bg-rose-50 p-3 text-sm text-rose-700" role="alert">
            {error}
            {needsVerification && (
              <Link
                to={`/verify-email?email=${encodeURIComponent(form.email)}`}
                className="ml-2 font-bold underline"
              >
                Verify now
              </Link>
            )}
          </div>
        )}
        <Button type="submit" loading={loading} className="w-full">
          Sign in <span>→</span>
        </Button>
        <div className="pt-3 text-center text-sm muted">
          New to mydoctorpal?{" "}
          <Link to="/register" className="font-bold text-forest">
            Create an account
          </Link>
        </div>
      </form>
    </AuthShell>
  );
}

import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import AuthShell from "./AuthShell";
import Input from "../../components/common/Input";
import Button from "../../components/common/Button";
import { authApi } from "../../api/authApi";
import { getApiError } from "../../utils/errorHandler";

export default function ForgotPassword() {
  const [email, setEmail] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const submit = async (event) => {
    event.preventDefault();
    setError("");
    setLoading(true);
    try {
      await authApi.forgotPassword({ email: email.trim() });
      navigate(`/verify-reset-otp?email=${encodeURIComponent(email.trim())}`);
    } catch (err) {
      setError(getApiError(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthShell title="Forgot your password?" subtitle="We'll email you a one-time code to reset it.">
      <form onSubmit={submit} className="space-y-4">
        <Input label="Email address" type="email" required value={email} onChange={(e) => setEmail(e.target.value)} />
        {error && (
          <div className="rounded-xl bg-rose-50 p-3 text-sm text-rose-700" role="alert">
            {error}
          </div>
        )}
        <Button type="submit" loading={loading} className="w-full">
          Send reset code →
        </Button>
        <div className="text-center text-sm">
          <Link to="/login" className="font-bold text-forest">
            Back to sign in
          </Link>
        </div>
      </form>
    </AuthShell>
  );
}

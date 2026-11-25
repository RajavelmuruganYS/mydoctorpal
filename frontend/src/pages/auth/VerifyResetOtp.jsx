import { useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import AuthShell from "./AuthShell";
import Input from "../../components/common/Input";
import Button from "../../components/common/Button";
import { authApi } from "../../api/authApi";
import { getApiError } from "../../utils/errorHandler";

export default function VerifyResetOtp() {
  const [params] = useSearchParams();
  const [form, setForm] = useState({ email: params.get("email") || "", otp: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const submit = async (event) => {
    event.preventDefault();
    setError("");
    setLoading(true);
    try {
      const email = form.email.trim();
      const otp = form.otp.trim();
      await authApi.verifyResetOtp({ email, otp });
      navigate(`/reset-password?email=${encodeURIComponent(email)}&otp=${encodeURIComponent(otp)}`);
    } catch (err) {
      setError(getApiError(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthShell title="Check your inbox." subtitle="Enter the 6-digit reset code we emailed you.">
      <form onSubmit={submit} className="space-y-4">
        <Input label="Email" type="email" required value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
        <Input
          label="Reset code"
          required
          inputMode="numeric"
          pattern="\d{6}"
          maxLength={6}
          title="6 digits"
          value={form.otp}
          onChange={(e) => setForm({ ...form, otp: e.target.value.replace(/\D/g, "") })}
        />
        {error && (
          <div className="rounded-xl bg-rose-50 p-3 text-sm text-rose-700" role="alert">
            {error}
          </div>
        )}
        <Button type="submit" loading={loading} className="w-full">
          Verify code →
        </Button>
        <div className="text-center text-sm">
          <Link to="/forgot-password" className="font-bold text-forest">
            Send a new code
          </Link>
        </div>
      </form>
    </AuthShell>
  );
}

import { useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import AuthShell from "./AuthShell";
import Input from "../../components/common/Input";
import Button from "../../components/common/Button";
import { authApi } from "../../api/authApi";
import { getApiError } from "../../utils/errorHandler";

export default function VerifyOtp() {
  const [params] = useSearchParams();
  const [form, setForm] = useState({ email: params.get("email") || "", otp: "" });
  const [error, setError] = useState("");
  const [info, setInfo] = useState("Enter the 6-digit code we emailed you.");
  const [loading, setLoading] = useState(false);
  const [resending, setResending] = useState(false);
  const navigate = useNavigate();

  const submit = async (event) => {
    event.preventDefault();
    setError("");
    setLoading(true);
    try {
      await authApi.verifyEmail({ email: form.email.trim(), otp: form.otp.trim() });
      navigate("/login");
    } catch (err) {
      setError(getApiError(err));
    } finally {
      setLoading(false);
    }
  };

  const resend = async () => {
    setError("");
    setResending(true);
    try {
      const response = await authApi.resendOtp({ email: form.email.trim() });
      setInfo(response.message || "A new code has been sent.");
    } catch (err) {
      setError(getApiError(err));
    } finally {
      setResending(false);
    }
  };

  return (
    <AuthShell title="Verify your email." subtitle={info}>
      <form onSubmit={submit} className="space-y-4">
        <Input
          label="Email"
          type="email"
          required
          value={form.email}
          onChange={(e) => setForm({ ...form, email: e.target.value })}
        />
        <Input
          label="One-time code"
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
          Verify email →
        </Button>
        <div className="flex items-center justify-between text-sm">
          <button type="button" onClick={resend} disabled={resending || !form.email} className="font-bold text-forest disabled:opacity-50">
            {resending ? "Sending…" : "Resend code"}
          </button>
          <Link to="/login" className="font-bold text-forest">
            Back to sign in
          </Link>
        </div>
      </form>
    </AuthShell>
  );
}

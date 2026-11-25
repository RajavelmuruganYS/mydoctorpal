import { useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import AuthShell from "./AuthShell";
import Input from "../../components/common/Input";
import Button from "../../components/common/Button";
import { authApi } from "../../api/authApi";
import { useToast } from "../../context/ToastContext";
import { getApiError, getFieldErrors } from "../../utils/errorHandler";

export default function ResetPassword() {
  const [params] = useSearchParams();
  const [form, setForm] = useState({
    email: params.get("email") || "",
    otp: params.get("otp") || "",
    newPassword: "",
    confirm: "",
  });
  const [error, setError] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const toast = useToast();

  const submit = async (event) => {
    event.preventDefault();
    setError("");
    if (form.newPassword !== form.confirm) {
      setError("The two passwords do not match.");
      return;
    }
    setLoading(true);
    try {
      const response = await authApi.resetPassword({
        email: form.email.trim(),
        otp: form.otp.trim(),
        newPassword: form.newPassword,
      });
      toast.success(response.message || "Password reset. Please sign in.");
      navigate("/login");
    } catch (err) {
      setError(getApiError(err));
      setFieldErrors(getFieldErrors(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthShell title="Choose a new password." subtitle="Use at least 8 characters.">
      <form onSubmit={submit} className="space-y-4">
        <Input label="Email" type="email" required value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
        <Input
          label="Reset code"
          required
          inputMode="numeric"
          maxLength={6}
          value={form.otp}
          onChange={(e) => setForm({ ...form, otp: e.target.value.replace(/\D/g, "") })}
        />
        <Input
          label="New password"
          type="password"
          required
          minLength={8}
          maxLength={100}
          autoComplete="new-password"
          error={fieldErrors.newPassword}
          value={form.newPassword}
          onChange={(e) => setForm({ ...form, newPassword: e.target.value })}
        />
        <Input
          label="Confirm new password"
          type="password"
          required
          autoComplete="new-password"
          value={form.confirm}
          onChange={(e) => setForm({ ...form, confirm: e.target.value })}
        />
        {error && (
          <div className="rounded-xl bg-rose-50 p-3 text-sm text-rose-700" role="alert">
            {error}
          </div>
        )}
        <Button type="submit" loading={loading} className="w-full">
          Reset password →
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

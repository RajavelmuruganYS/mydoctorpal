import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import AuthShell from "./AuthShell";
import Input from "../../components/common/Input";
import Button from "../../components/common/Button";
import { authApi } from "../../api/authApi";
import { getApiError, getFieldErrors } from "../../utils/errorHandler";
import { compact } from "../../utils/payload";

export default function Register() {
  const [form, setForm] = useState({ email: "", password: "", firstName: "", lastName: "", phone: "" });
  const [error, setError] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const bind = (name) => ({
    value: form[name],
    error: fieldErrors[name],
    onChange: (e) => setForm({ ...form, [name]: e.target.value }),
  });

  const submit = async (event) => {
    event.preventDefault();
    setError("");
    setFieldErrors({});
    setLoading(true);
    try {
      await authApi.register(compact({ ...form, email: form.email.trim(), phone: form.phone.trim() }));
      navigate(`/verify-email?email=${encodeURIComponent(form.email.trim())}`);
    } catch (err) {
      setError(getApiError(err));
      setFieldErrors(getFieldErrors(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthShell
      title="Create your care space."
      subtitle="A simple, secure home for everything that matters in your health."
    >
      <form onSubmit={submit} className="space-y-4">
        <div className="grid gap-4 sm:grid-cols-2">
          <Input label="First name" required {...bind("firstName")} />
          <Input label="Last name" required {...bind("lastName")} />
        </div>
        <Input label="Email address" type="email" required autoComplete="username" {...bind("email")} />
        <Input label="Phone (optional)" {...bind("phone")} />
        <Input
          label="Password"
          type="password"
          minLength={8}
          maxLength={100}
          required
          autoComplete="new-password"
          hint="8–100 characters."
          {...bind("password")}
        />
        {error && (
          <div className="rounded-xl bg-rose-50 p-3 text-sm text-rose-700" role="alert">
            {error}
          </div>
        )}
        <Button type="submit" loading={loading} className="w-full">
          Create account →
        </Button>
        <div className="text-center text-sm muted">
          Already have an account?{" "}
          <Link to="/login" className="font-bold text-forest">
            Sign in
          </Link>
        </div>
      </form>
    </AuthShell>
  );
}

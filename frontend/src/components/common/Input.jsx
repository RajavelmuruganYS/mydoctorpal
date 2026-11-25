export default function Input({ label, error, hint, required, className = "", ...props }) {
  return (
    <label className={`block space-y-1.5 ${className}`}>
      <span className="text-sm font-semibold text-ink">
        {label}
        {required && <span className="ml-0.5 text-rose-600">*</span>}
      </span>
      <input className={`field ${error ? "border-rose-400" : ""}`} required={required} {...props} />
      {hint && !error && <span className="block text-xs muted">{hint}</span>}
      {error && <span className="block text-xs font-semibold text-rose-600">{error}</span>}
    </label>
  );
}

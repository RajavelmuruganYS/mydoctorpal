import LoadingSpinner from "./LoadingSpinner";

const VARIANTS = {
  primary: "btn-primary",
  secondary: "btn-secondary",
  danger:
    "inline-flex items-center justify-center gap-2 rounded-[14px] bg-rose-600 px-[18px] py-3 font-bold text-white transition hover:bg-rose-700",
};

export default function Button({
  children,
  loading = false,
  variant = "primary",
  className = "",
  type = "button",
  disabled = false,
  ...props
}) {
  return (
    <button
      {...props}
      type={type}
      disabled={loading || disabled}
      className={`${VARIANTS[variant] || VARIANTS.primary} ${className} disabled:cursor-not-allowed disabled:opacity-50`}
    >
      {loading && <LoadingSpinner small />}
      {children}
    </button>
  );
}

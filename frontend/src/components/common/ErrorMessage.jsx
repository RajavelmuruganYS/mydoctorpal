export default function ErrorMessage({
  message = "We could not load this information.",
  onRetry,
}) {
  return (
    <div className="rounded-2xl border border-rose-100 bg-rose-50 p-5 text-sm text-rose-700">
      <strong>Something needs attention.</strong>
      <p className="mt-1">{message}</p>
      {onRetry && (
        <button onClick={onRetry} className="mt-3 font-bold underline">
          Try again
        </button>
      )}
    </div>
  );
}

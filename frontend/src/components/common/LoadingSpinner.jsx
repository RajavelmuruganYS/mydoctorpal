export default function LoadingSpinner({ small = false }) {
  return (
    <span
      className={`inline-block animate-spin rounded-full border-2 border-forest/20 border-t-forest ${small ? "h-4 w-4" : "h-8 w-8"}`}
      aria-label="Loading"
    />
  );
}

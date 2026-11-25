import { Inbox } from "lucide-react";

export default function EmptyState({
  title = "Nothing here yet",
  message = "Records will appear here once they exist.",
  action,
}) {
  return (
    <div className="grid place-items-center rounded-2xl border border-dashed border-forest/15 bg-sage/40 p-10 text-center">
      <Inbox className="mb-3 text-forest/50" size={30} />
      <h3 className="font-bold">{title}</h3>
      <p className="mt-1 max-w-md text-sm muted">{message}</p>
      {action}
    </div>
  );
}

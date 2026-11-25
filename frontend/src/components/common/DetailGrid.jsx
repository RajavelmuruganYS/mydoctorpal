/** items: [{ label, value }] — empty values render as an em dash. */
export default function DetailGrid({ items, columns = "sm:grid-cols-2 lg:grid-cols-3" }) {
  return (
    <div className={`grid gap-4 ${columns}`}>
      {items.map((item) => (
        <div key={item.label} className="rounded-2xl bg-sage/50 p-4">
          <p className="text-xs font-bold uppercase tracking-wider text-forest/50">{item.label}</p>
          <div className="mt-1.5 break-words text-sm font-semibold">
            {item.value === null || item.value === undefined || item.value === "" ? "—" : item.value}
          </div>
        </div>
      ))}
    </div>
  );
}

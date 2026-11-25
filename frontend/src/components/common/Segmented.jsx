/** Filter pills. options: ["A","B"] or [{ value, label }] */
export default function Segmented({ options, value, onChange }) {
  return (
    <div className="inline-flex flex-wrap gap-1 rounded-2xl bg-sage p-1">
      {options.map((option) => {
        const item = typeof option === "string" ? { value: option, label: option } : option;
        const active = item.value === value;
        return (
          <button
            key={item.value}
            type="button"
            onClick={() => onChange(item.value)}
            className={`rounded-xl px-3.5 py-2 text-xs font-bold transition ${
              active ? "bg-white text-forest shadow-card" : "text-forest/60 hover:text-forest"
            }`}
          >
            {String(item.label).replaceAll("_", " ")}
          </button>
        );
      })}
    </div>
  );
}

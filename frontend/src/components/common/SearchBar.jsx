import { Search } from "lucide-react";

export default function SearchBar({ value, onChange, placeholder = "Search…" }) {
  return (
    <div className="relative">
      <Search size={17} className="absolute left-3 top-3.5 text-forest/40" />
      <input
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        className="field pl-10"
        aria-label={placeholder}
      />
    </div>
  );
}

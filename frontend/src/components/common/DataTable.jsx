import { Link } from "react-router-dom";

/**
 * columns: [{ key, label, render?(row) }]
 * rowLink: (row) => path   — makes the first column a link to the record.
 */
export default function DataTable({ columns, rows = [], empty = null, rowLink }) {
  if (!rows.length) return empty;
  return (
    <div className="overflow-x-auto">
      <table className="w-full min-w-[680px] text-left text-sm">
        <thead>
          <tr className="border-b border-forest/10 text-xs uppercase tracking-wider text-forest/50">
            {columns.map((c) => (
              <th key={c.key} className="px-4 py-3 font-bold">
                {c.label}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, i) => (
            <tr
              key={row.id ?? row.userId ?? i}
              className="border-b border-forest/5 last:border-0 hover:bg-sage/40"
            >
              {columns.map((c, index) => {
                const content = c.render ? c.render(row) : (row[c.key] ?? "—");
                return (
                  <td key={c.key} className="px-4 py-4">
                    {rowLink && index === 0 ? (
                      <Link to={rowLink(row)} className="font-bold text-forest underline-offset-2 hover:underline">
                        {content}
                      </Link>
                    ) : (
                      content
                    )}
                  </td>
                );
              })}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

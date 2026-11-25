/** Case-insensitive match of a query against selected fields of a row. */
export const matches = (row, fields, query) => {
  const q = (query || "").trim().toLowerCase();
  if (!q) return true;
  return fields.some((field) => String(row[field] ?? "").toLowerCase().includes(q));
};

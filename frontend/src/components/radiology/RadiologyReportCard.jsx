import DataTable from "../common/DataTable";
export default function RadiologyReportCard({ rows = [], empty }) {
  return (
    <DataTable
      rows={rows}
      empty={empty}
      columns={[
        { key: "id", label: "ID" },
        { key: "status", label: "Status" },
        { key: "createdAt", label: "Updated" },
      ]}
    />
  );
}

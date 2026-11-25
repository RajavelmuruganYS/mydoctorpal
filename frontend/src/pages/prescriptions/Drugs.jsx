import { useState } from "react";
import { Plus } from "lucide-react";
import PageHeader from "../../components/common/PageHeader";
import DataTable from "../../components/common/DataTable";
import EmptyState from "../../components/common/EmptyState";
import Async from "../../components/common/Async";
import Segmented from "../../components/common/Segmented";
import SearchBar from "../../components/common/SearchBar";
import StatusBadge from "../../components/common/StatusBadge";
import Modal from "../../components/common/Modal";
import Input from "../../components/common/Input";
import Button from "../../components/common/Button";
import { useApi } from "../../hooks/useApi";
import { useDebounce } from "../../hooks/useDebounce";
import { useForm } from "../../hooks/useForm";
import { useMutation } from "../../hooks/useMutation";
import { useAuth } from "../../context/AuthContext";
import { prescriptionApi } from "../../api/prescriptionApi";
import { asList } from "../../utils/payload";

function CreateDrugModal({ open, onClose, onSaved }) {
  const form = useForm({ brandName: "", genericName: "", dosageForm: "", strength: "" });
  const save = useMutation(prescriptionApi.createDrug, { success: "Drug added to the catalogue." });
  const submit = async (event) => {
    event.preventDefault();
    if (await save.run(form.values)) {
      form.setValues({ brandName: "", genericName: "", dosageForm: "", strength: "" });
      onSaved();
    }
  };
  return (
    <Modal open={open} onClose={onClose} title="Add drug">
      <form onSubmit={submit} className="space-y-4">
        <Input label="Brand name" required maxLength={200} {...form.bind("brandName", save.fieldErrors)} />
        <Input label="Generic name" required maxLength={200} {...form.bind("genericName", save.fieldErrors)} />
        <div className="grid gap-4 sm:grid-cols-2">
          <Input label="Dosage form" required maxLength={100} placeholder="Tablet, Capsule…" {...form.bind("dosageForm", save.fieldErrors)} />
          <Input label="Strength" required maxLength={100} placeholder="500 mg" {...form.bind("strength", save.fieldErrors)} />
        </div>
        <div className="flex justify-end gap-3">
          <Button variant="secondary" onClick={onClose}>Cancel</Button>
          <Button type="submit" loading={save.loading}>Add drug</Button>
        </div>
      </form>
    </Modal>
  );
}

export default function Drugs() {
  const { hasRole } = useAuth();
  const canManage = hasRole("ADMIN", "PHARMACIST");
  const [mode, setMode] = useState("brand");
  const [query, setQuery] = useState("");
  const [open, setOpen] = useState(false);
  const term = useDebounce(query, 350).trim();

  const state = useApi(() => {
    if (!term) return prescriptionApi.drugs();
    return mode === "brand" ? prescriptionApi.searchBrand(term) : prescriptionApi.searchGeneric(term);
  }, [term, mode]);
  const toggle = useMutation((drug) => (drug.active ? prescriptionApi.deactivateDrug(drug.id) : prescriptionApi.activateDrug(drug.id)));

  const flip = async (drug) => {
    if (await toggle.run(drug)) state.reload();
  };

  return (
    <>
      <PageHeader
        eyebrow="Pharmacy"
        title="Drug catalogue"
        description="Medicines that can be prescribed and dispensed."
        actions={canManage && <Button onClick={() => setOpen(true)}><Plus size={16} /> Add drug</Button>}
      />
      <div className="surface p-5">
        <div className="mb-4 flex flex-wrap items-center gap-4">
          <div className="w-full max-w-sm">
            <SearchBar value={query} onChange={setQuery} placeholder={`Search by ${mode} name…`} />
          </div>
          <Segmented options={[{ value: "brand", label: "Brand" }, { value: "generic", label: "Generic" }]} value={mode} onChange={setMode} />
        </div>
        <Async state={state}>
          {(data) => (
            <DataTable
              rows={asList(data)}
              empty={<EmptyState title="No drugs found" message={term ? "No drug matches this search." : "The catalogue is empty."} />}
              columns={[
                { key: "brandName", label: "Brand" },
                { key: "genericName", label: "Generic" },
                { key: "dosageForm", label: "Form" },
                { key: "strength", label: "Strength" },
                { key: "active", label: "Status", render: (d) => <StatusBadge status={d.active ? "ACTIVE" : "INACTIVE"} /> },
                ...(canManage
                  ? [{ key: "actions", label: "", render: (d) => (
                      <Button variant="secondary" onClick={() => flip(d)} loading={toggle.loading}>
                        {d.active ? "Deactivate" : "Activate"}
                      </Button>
                    ) }]
                  : []),
              ]}
            />
          )}
        </Async>
      </div>
      <CreateDrugModal open={open} onClose={() => setOpen(false)} onSaved={() => { setOpen(false); state.reload(); }} />
    </>
  );
}

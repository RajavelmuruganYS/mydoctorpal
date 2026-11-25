import { Link } from "react-router-dom";
import Button from "./Button";

export default function FormActions({ loading, cancelTo, submitLabel = "Save" }) {
  return (
    <div className="flex justify-end gap-3 pt-2">
      {cancelTo && (
        <Link to={cancelTo} className="btn-secondary">
          Cancel
        </Link>
      )}
      <Button type="submit" loading={loading}>
        {submitLabel}
      </Button>
    </div>
  );
}

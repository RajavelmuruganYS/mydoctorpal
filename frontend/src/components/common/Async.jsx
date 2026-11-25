import LoadingPage from "./LoadingPage";
import ErrorMessage from "./ErrorMessage";
import { getApiError } from "../../utils/errorHandler";

/** Renders loading / error states for a useApi() result, then children(data). */
export default function Async({ state, children, inline = false }) {
  if (state.loading && !state.data) {
    return inline ? <div className="p-8 text-center muted">Loading…</div> : <LoadingPage />;
  }
  if (state.error) {
    return <ErrorMessage message={getApiError(state.error)} onRetry={state.reload} />;
  }
  return children(state.data);
}

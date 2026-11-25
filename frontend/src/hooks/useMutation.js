import { useCallback, useState } from "react";
import { useToast } from "../context/ToastContext";
import { getApiError, getFieldErrors } from "../utils/errorHandler";

/**
 * Wrap a write request with loading state, toast feedback and field errors.
 *   const save = useMutation(patientApi.create, { success: "Patient created" });
 *   const res = await save.run(payload);   // resolves to the response, or null on failure
 */
export const useMutation = (request, { success, silent = false } = {}) => {
  const toast = useToast();
  const [state, setState] = useState({ loading: false, error: null });

  const run = useCallback(
    async (...args) => {
      setState({ loading: true, error: null });
      try {
        const response = await request(...args);
        setState({ loading: false, error: null });
        if (!silent) toast.success(success || response.message || "Saved successfully.");
        return response;
      } catch (error) {
        setState({ loading: false, error });
        if (!silent) toast.error(getApiError(error));
        return null;
      }
    },
    [request, success, silent, toast],
  );

  return {
    run,
    loading: state.loading,
    error: state.error,
    fieldErrors: getFieldErrors(state.error),
    status: state.error?.response?.status,
  };
};

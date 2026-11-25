import { useCallback, useEffect, useRef, useState } from "react";

/**
 * Load data from the API.
 *   const { data, loading, error, reload } = useApi(() => patientApi.get(id), [id]);
 * `enabled=false` skips the request (e.g. until an id is known).
 */
export const useApi = (request, deps = [], enabled = true) => {
  const requestRef = useRef(request);
  requestRef.current = request;
  const sequence = useRef(0);
  const [state, setState] = useState({ data: null, loading: enabled, error: null });

  const run = useCallback(async () => {
    const current = ++sequence.current;
    setState((s) => ({ ...s, loading: true, error: null }));
    try {
      const response = await requestRef.current();
      if (current === sequence.current) {
        setState({ data: response.data, loading: false, error: null });
      }
      return response.data;
    } catch (error) {
      if (current === sequence.current) {
        setState((s) => ({ ...s, loading: false, error }));
      }
      return undefined;
    }
  }, []);

  useEffect(() => {
    if (enabled) run();
    else setState({ data: null, loading: false, error: null });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [enabled, run, ...deps]);

  return { ...state, reload: run };
};

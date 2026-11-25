import { useCallback, useState } from "react";

/**
 * Tiny controlled-form helper.
 *   const form = useForm({ name: "" });
 *   <Input label="Name" {...form.bind("name")} />
 * Extra field errors (e.g. from the backend) can be passed to bind() via `serverErrors`.
 */
export const useForm = (initial) => {
  const [values, setValues] = useState(initial);
  const [errors, setErrors] = useState({});

  const set = useCallback((name, value) => {
    setValues((current) => ({ ...current, [name]: value }));
    setErrors((current) => (current[name] ? { ...current, [name]: undefined } : current));
  }, []);

  const bind = (name, serverErrors = {}) => ({
    name,
    value: values[name] ?? "",
    onChange: (event) => set(name, event.target.value),
    error: errors[name] || serverErrors[name],
  });

  return { values, set, bind, errors, setErrors, setValues };
};

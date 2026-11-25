export const required = (value, label) =>
  value ? null : `${label} is required`;
export const email = (value) =>
  /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value || "")
    ? null
    : "Enter a valid email address";
export const password = (value) =>
  value && value.length >= 8 ? null : "Password must be at least 8 characters";
export const validateFields = (values, rules) =>
  Object.entries(rules).reduce((errors, [key, fn]) => {
    const error = fn(values[key], values);
    return error ? { ...errors, [key]: error } : errors;
  }, {});

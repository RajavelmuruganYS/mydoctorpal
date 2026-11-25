/** Remove empty values so optional fields are omitted instead of sent as "". */
export const compact = (object) =>
  Object.fromEntries(
    Object.entries(object).filter(
      ([, value]) => value !== "" && value !== null && value !== undefined,
    ),
  );

export const toInt = (value) =>
  value === "" || value === null || value === undefined ? undefined : parseInt(value, 10);

export const toNumber = (value) =>
  value === "" || value === null || value === undefined ? undefined : Number(value);

export const asList = (data) => (Array.isArray(data) ? data : []);

export const personName = (person) =>
  person
    ? [person.firstName, person.lastName].filter(Boolean).join(" ").trim()
    : "";

/** Treat "404 not found" as an empty list (e.g. no report written yet). */
export const emptyOn404 = (promise) =>
  promise.catch((error) =>
    error?.response?.status === 404 ? { data: [] } : Promise.reject(error),
  );

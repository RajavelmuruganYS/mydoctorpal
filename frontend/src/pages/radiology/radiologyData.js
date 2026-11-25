import { asList } from "../../utils/payload";

/** Endpoints like /radiology/reports/orders/{id} may return one object or a list. */
export const toArray = (data) => (Array.isArray(data) ? data : data && typeof data === "object" && Object.keys(data).length ? [data] : []);

export { asList };

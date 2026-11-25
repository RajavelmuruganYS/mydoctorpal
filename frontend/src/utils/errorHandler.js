export const getApiError = (error) => {
  const data = error?.response?.data;
  if (data?.message) return data.message;
  if (typeof data === "string" && data.trim()) return data;
  if (data?.error) return data.error;
  if (error?.code === "ERR_NETWORK") {
    return "Cannot reach the server. Make sure the backend is running on port 8080.";
  }
  return error?.message || "Something went wrong. Please try again.";
};

/** Field-level validation messages from the backend: { fieldName: "message" } */
export const getFieldErrors = (error) => error?.response?.data?.errors || {};

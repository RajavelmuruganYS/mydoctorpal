const KEY = "mydoctorpal.session";
export const getSession = () => {
  try {
    return JSON.parse(localStorage.getItem(KEY) || "null");
  } catch {
    return null;
  }
};
export const setSession = (data) => {
  if (data == null) {
    localStorage.removeItem(KEY);
    return;
  }
  localStorage.setItem(KEY, JSON.stringify(data));
};
export const clearSession = () => localStorage.removeItem(KEY);
export const updateSession = (patch) => {
  const current = getSession();
  if (!current && !patch) {
    clearSession();
    return;
  }
  setSession({ ...(current || {}), ...patch });
};

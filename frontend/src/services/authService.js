import { authApi } from "../api/authApi";
import { getSession, setSession, clearSession } from "./storageService";

export const signIn = async (credentials) => {
  const { data } = await authApi.login(credentials);
  setSession(data);
  return data;
};

export const signOut = async () => {
  const refreshToken = getSession()?.refreshToken;
  try {
    if (refreshToken) await authApi.logout({ refreshToken });
  } catch {
    /* the local session is cleared regardless */
  } finally {
    clearSession();
  }
};

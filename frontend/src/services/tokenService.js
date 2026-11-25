import { getSession, updateSession } from "./storageService";
export const getAccessToken = () => getSession()?.accessToken;
export const getRefreshToken = () => getSession()?.refreshToken;
export const saveTokens = (accessToken, refreshToken) =>
  updateSession({
    accessToken,
    refreshToken: refreshToken || getRefreshToken(),
  });

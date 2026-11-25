import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { notificationApi } from "../api/notificationApi";
import { useAuth } from "./AuthContext";

const NotificationContext = createContext({ count: 0, refresh: () => {} });

export function NotificationProvider({ children }) {
  const { isAuthenticated, authInitializing } = useAuth();
  const [count, setCount] = useState(0);

  const refresh = useCallback(async () => {
    if (!isAuthenticated || authInitializing) return;
    try {
      const { data } = await notificationApi.count();
      setCount(Number(data?.count ?? data ?? 0) || 0);
    } catch {
      /* the badge is non-critical */
    }
  }, [authInitializing, isAuthenticated]);

  useEffect(() => {
    if (!isAuthenticated || authInitializing) return undefined;
    refresh();
    const timer = setInterval(refresh, 60000);
    return () => clearInterval(timer);
  }, [authInitializing, isAuthenticated, refresh]);

  const value = useMemo(() => ({ count, refresh }), [count, refresh]);
  return <NotificationContext.Provider value={value}>{children}</NotificationContext.Provider>;
}

export const useNotifications = () => useContext(NotificationContext);

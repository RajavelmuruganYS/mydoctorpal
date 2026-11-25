import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { clearSession, getSession, setSession } from "../services/storageService";
import { signIn, signOut } from "../services/authService";
import { userApi } from "../api/userApi";
import { patientApi } from "../api/patientApi";
import { primaryRole } from "../utils/roles";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [session, setCurrent] = useState(null);
  const [authInitializing, setAuthInitializing] = useState(true);
  const [rolesReady, setRolesReady] = useState(false);
  const [patient, setPatient] = useState(null);
  const [patientState, setPatientState] = useState({ loading: false, error: null });

  const roles = useMemo(() => session?.roles || session?.user?.roles || [], [session]);
  const role = primaryRole(roles);
  const isPatient = roles.includes("PATIENT") && role === "PATIENT";
  const isAuthenticated = Boolean(session?.accessToken);

  // Refresh the profile from the backend on start-up.
  useEffect(() => {
    const current = getSession();
    if (!current?.accessToken) {
      setCurrent(null);
      setRolesReady(false);
      setAuthInitializing(false);
      return;
    }
    userApi
      .me()
      .then(({ data }) => {
        const next = { ...getSession(), user: data, roles: data.roles || current.roles };
        setSession(next);
        setCurrent(next);
        setRolesReady(Array.isArray(next.roles) && next.roles.length > 0);
      })
      .catch(() => {
        clearSession();
        setCurrent(null);
        setRolesReady(false);
      })
      .finally(() => setAuthInitializing(false));
  }, []);

  const loadPatient = useCallback(async () => {
    setPatientState({ loading: true, error: null });
    try {
      const { data } = await patientApi.me();
      setPatient(data);
      setPatientState({ loading: false, error: null });
    } catch (error) {
      setPatient(null);
      setPatientState({ loading: false, error });
    }
  }, []);

  // A patient user needs their patient record (its id is the patientId used by most APIs).
  useEffect(() => {
    if (authInitializing) return;
    if (isAuthenticated && isPatient) loadPatient();
    else {
      setPatient(null);
      setPatientState({ loading: false, error: null });
    }
  }, [authInitializing, isAuthenticated, isPatient, loadPatient]);

  const login = async (credentials) => {
    setAuthInitializing(true);
    setRolesReady(false);
    setCurrent(null);
    setPatient(null);
    setPatientState({ loading: false, error: null });
    try {
      const data = await signIn(credentials);
      const authenticatedSession = getSession();
      const { data: profile } = await userApi.me();
      const next = { ...authenticatedSession, user: profile, roles: profile.roles || data.roles };
      setSession(next);
      setCurrent(next);
      setRolesReady(Array.isArray(next.roles) && next.roles.length > 0);
      return data;
    } finally {
      setAuthInitializing(false);
    }
  };

  const logout = async () => {
    await signOut();
    setAuthInitializing(false);
    setRolesReady(false);
    setCurrent(null);
    setPatient(null);
    setPatientState({ loading: false, error: null });
  };

  const value = useMemo(
    () => ({
      session,
      user: { ...session, ...session?.user },
      userId: session?.userId ?? session?.user?.userId ?? null,
      loading: authInitializing,
      authInitializing,
      rolesReady,
      isAuthenticated,
      login,
      logout,
      roles,
      role,
      isPatient,
      hasRole: (...list) => list.flat().some((r) => roles.includes(r)),
      patient,
      patientId: patient?.id ?? null,
      patientState,
      reloadPatient: loadPatient,
    }),
    [session, authInitializing, rolesReady, roles, role, isPatient, isAuthenticated, patient, patientState, loadPatient],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);

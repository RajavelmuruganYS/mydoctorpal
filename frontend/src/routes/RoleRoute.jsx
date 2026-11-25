import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import LoadingPage from "../components/common/LoadingPage";

export default function RoleRoute({ roles, children }) {
  const { authInitializing, isAuthenticated, rolesReady, hasRole } = useAuth();
  if (authInitializing || (isAuthenticated && !rolesReady)) return <LoadingPage />;
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  return hasRole(roles) ? children : <Navigate to="/403" replace />;
}

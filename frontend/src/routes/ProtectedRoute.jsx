import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import LoadingPage from "../components/common/LoadingPage";
export default function ProtectedRoute({ children }) {
  const { isAuthenticated, authInitializing } = useAuth();
  const location = useLocation();
  if (authInitializing) return <LoadingPage />;
  return isAuthenticated ? (
    children
  ) : (
    <Navigate to="/login" replace state={{ from: location }} />
  );
}

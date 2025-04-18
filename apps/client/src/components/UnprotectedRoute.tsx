import { Navigate } from "react-router-dom";
import { useIsAuthenticated } from "../context/AuthContext";
import { JSX } from "react";

const UnprotectedRoute = ({ children }: { children: JSX.Element }) => {
  const isAuthenticated = useIsAuthenticated();

  if (isAuthenticated) {
    return <Navigate to="/my-trades" replace />;
  }

  return children;
};

export default UnprotectedRoute;

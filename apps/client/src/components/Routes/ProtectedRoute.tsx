import { Navigate } from "react-router-dom";
import { useIsAuthenticated } from "../../context/AuthContext";
import { PropsWithChildren } from "react";
import { Container, Stack } from "@mui/material";
import NavBar from "../NavBar";

const ProtectedRoute = ({ children }: PropsWithChildren) => {
  const isAuthenticated = useIsAuthenticated();

  if (!isAuthenticated) {
    return <Navigate to="/sign-in" replace />;
  }

  return (
    <Stack width="100%" flexDirection="row">
      <NavBar />
      <Container sx={{ marginY: "2rem" }}>{children}</Container>
    </Stack>
  );
};

export default ProtectedRoute;

import { Button, Stack, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import {
  LogoutRounded,
  Person2Outlined,
  Schedule,
  TrendingUp,
} from "@mui/icons-material";
import { signOut } from "firebase/auth";
import { auth } from "../firebaseConfig/firebase";

function NavBar({ currentPage }: { currentPage?: string }) {
  const navigate = useNavigate();

  return (
    <Stack
      height="100%"
      minWidth="6rem"
      maxWidth="6rem"
      gap="2rem"
      justifyContent="center"
      alignItems="center"
      sx={{ backgroundColor: "#5533ff22", borderRadius: "0 2rem 2rem 0" }}
    >
      <Button
        onClick={() => {
          navigate("/make-trades");
        }}
        sx={{
          flexDirection: "column",
          width: "80%",
          marginTop: "auto",

          color: currentPage === "make-trades" ? "ffffff" : "#7e7e7e",
        }}
      >
        <TrendingUp sx={{ width: "3rem", height: "3rem" }} />
        <Typography>Make Trades</Typography>
      </Button>
      <Button
        onClick={() => {
          navigate("/my-trades");
        }}
        sx={{
          flexDirection: "column",
          width: "80%",
          color: currentPage === "my-trades" ? "ffffff" : "#7e7e7e",
        }}
      >
        <Schedule sx={{ width: "3rem", height: "3rem" }} />
        <Typography>My Trades</Typography>
      </Button>
      <Button
        onClick={async () => {
          await signOut(auth); // Sign out the user
          navigate("/sign-in"); // Redirect to sign-in
        }}
        sx={{
          flexDirection: "column",
          width: "80%",
          color: "#7e7e7e",
          marginTop: "auto",
          paddingBottom: "2rem",
        }}
      >
        <LogoutRounded sx={{ width: "3rem", height: "3rem" }} />
        <Typography>Sign Out</Typography>
      </Button>
    </Stack>
  );
}

export default NavBar;

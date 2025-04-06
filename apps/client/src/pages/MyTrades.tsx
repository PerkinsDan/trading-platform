import { Stack, Typography } from "@mui/material";
import NavBar from "../components/NavBar";

function MyTrades() {
  return (
    <Stack height="100%" width="100%" flexDirection="row">
      <NavBar currentPage="my-trades" />
      <Typography>My Trades</Typography>
    </Stack>
  );
}

export default MyTrades;

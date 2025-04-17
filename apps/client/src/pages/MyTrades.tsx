import { Stack, Typography } from "@mui/material";
import NavBar from "../components/NavBar";
import TradesPendingList from "../components/TradesList/TradesPendingList";
import TradesHistoryList from "../components/TradesList/TradesHistoryList";
import Balance from "../components/Balance";

function MyTrades() {
  return (
    <Stack height="100%" width="100%" flexDirection="row">
      <NavBar currentPage="my-trades" />
      <Stack padding="2rem" width="100%">
        <Typography variant="h4" marginBottom="2rem">
          My Trades
        </Typography>
        <Balance />
        <TradesPendingList />
        <TradesHistoryList />
      </Stack>
    </Stack>
  );
}

export default MyTrades;

import { Stack, Typography } from "@mui/material";
import TradesPendingList from "../components/TradesList/TradesPendingList";
import TradesHistoryList from "../components/TradesList/TradesHistoryList";
import Balance from "../components/Balance";

function MyTrades() {
  return (
    <Stack>
      <Typography variant="h4" marginBottom="2rem">
        My Trades
      </Typography>
      <Balance />
      <TradesPendingList />
      <TradesHistoryList />
    </Stack>
  );
}

export default MyTrades;

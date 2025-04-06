import { Stack, Typography } from "@mui/material";
import NavBar from "../components/NavBar";
import TradeList from "../components/TradeList";
import BalanceSummary from "../components/BalanceSummary";

function MyTrades() {
  const currentTrades = [
    { ticker: "Amazon", quantity: 10, price: 150.0 },
    { ticker: "Google", quantity: 5, price: 2800.0 },
  ];
  const oldTrades = [
    { ticker: "Netflix", quantity: 8, price: 500.0 },
    { ticker: "Meta", quantity: 12, price: 300.0 },
  ];
  const totalBalance = 10000.0;
  const lockedFunds = 5000.0;

  return (
    <Stack height="100%" width="100%" flexDirection="row">
      <NavBar currentPage="my-trades" />
      <Stack padding="2rem" width="100%">
        <Typography variant="h4" marginBottom="2rem">
          My Trades
        </Typography>
        <BalanceSummary totalBalance={totalBalance} lockedFunds={lockedFunds} />
        <Typography variant="h5" marginTop="2rem">
          Current Trades
        </Typography>
        <TradeList trades={currentTrades} />
        <Typography variant="h5" marginTop="2rem">
          Old Trades
        </Typography>
        <TradeList trades={oldTrades} />
      </Stack>
    </Stack>
  );
}

export default MyTrades;

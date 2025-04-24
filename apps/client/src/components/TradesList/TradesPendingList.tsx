import { Typography } from "@mui/material";
import TradeList from "./TradesList";
import useFetchTrades from "../../hooks/useFetchTrades";
import { TRADES_PENDING_ENDPOINT } from "../../constants/endpoints";

const TradesPendingList = () => {
  const tradesPending = useFetchTrades(TRADES_PENDING_ENDPOINT);

  return (
    <>
      <Typography variant="h5" marginTop="2rem">
        Pending Trades
      </Typography>
      {tradesPending.length > 0 ? (
        <TradeList trades={tradesPending} />
      ) : (
        <Typography variant="body1" color="textSecondary">
          No current trades.
        </Typography>
      )}
    </>
  );
};

export default TradesPendingList;

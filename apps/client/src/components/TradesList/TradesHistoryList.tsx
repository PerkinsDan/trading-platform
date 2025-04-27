import { Typography } from "@mui/material";
import TradeList from "./TradesList";
import useFetchTrades from "../../hooks/useFetchTrades";
import { TRADES_HISTORY_ENDPOINT } from "../../constants/endpoints";

const TradesHistoryList = () => {
  const tradesHistory = useFetchTrades(TRADES_HISTORY_ENDPOINT);

  return (
    <>
      <Typography variant="h5" marginTop="2rem">
        Trade History
      </Typography>
      {tradesHistory.length > 0 ? (
        <TradeList trades={tradesHistory} />
      ) : (
        <Typography variant="body1" color="textSecondary">
          No past trades.
        </Typography>
      )}
    </>
  );
};

export default TradesHistoryList;

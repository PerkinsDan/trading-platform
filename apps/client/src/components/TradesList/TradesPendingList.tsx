import { Typography } from "@mui/material";
import TradeList from "./TradesList";
import useFetchTrades from "../../hooks/useFetchTrades";
import {
  CANCEL_ORDER_ENDPOINT,
  TRADES_PENDING_ENDPOINT,
} from "../../constants/endpoints";
import { useAuth } from "../../context/AuthContext";
import { useState } from "react";
import { Trade } from "../../../types";

const TradesPendingList = () => {
  const [reloadFlag, setReloadFlag] = useState(0);
  const tradesPending = useFetchTrades(TRADES_PENDING_ENDPOINT, reloadFlag);
  const currentUser = useAuth();
  const [, setReload] = useState(0);

  const handleCancel = async (trade: Trade) => {
    if (!currentUser) {
      console.error("User not authenticated");
      return;
    }

    try {
      const response = await fetch(CANCEL_ORDER_ENDPOINT, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          orderId: trade.orderId,
          userId: currentUser.uid,
          ticker: trade.ticker,
          type: trade.type,
        }),
      });
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to cancel order: ${errorText}`);
      }
      alert("Order cancelled");
      setReloadFlag((f) => f + 1);
    } catch (error) {
      console.error("Error cancelling order:", error);
    }
    setReload((c) => c + 1);
  };

  return (
    <>
      <Typography variant="h5" marginTop="2rem">
        Pending Trades
      </Typography>
      {tradesPending.length > 0 ? (
        <TradeList trades={tradesPending} onCancel={handleCancel} />
      ) : (
        <Typography variant="body1" color="textSecondary">
          No current trades.
        </Typography>
      )}
    </>
  );
};

export default TradesPendingList;

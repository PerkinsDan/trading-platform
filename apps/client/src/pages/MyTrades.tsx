import { Stack, Typography, Button } from "@mui/material";
import NavBar from "../components/NavBar";
import TradeList from "../components/TradeList";
import BalanceSummary from "../components/BalanceSummary";
import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";

function MyTrades() {
  const [currentTrades, setCurrentTrades] = useState([]);
  const [oldTrades, setOldTrades] = useState([]);
  const [totalBalance, setTotalBalance] = useState(0);
  const currentUser = useAuth();
  const [updateCounter, setUpdateCounter] = useState(0);

  useEffect(() => {
    const fetchData = async () => {
      if (!currentUser) {
        console.error("User not authenticated");
        return;
      }

      const userId = currentUser.uid;

      try {
        const activePositionsResponse = await fetch(
          `${process.env.REACT_APP_API_BASE_URL}/user-active-positions?userId=${userId}`,
        );

        if (!activePositionsResponse.ok) {
          const errorText = await activePositionsResponse.text();
          throw new Error(
            `Failed to fetch user active positions data: ${errorText}`,
          );
        }

        setCurrentTrades((await activePositionsResponse.json()) || []);
      } catch (error) {
        console.log(error);
      }
      try {
        const userTradeHistoryResponse = await fetch(
          `${process.env.REACT_APP_API_BASE_URL}/user-trade-history?userId=${userId}`,
        );
        if (!userTradeHistoryResponse.ok) {
          const errorText = await userTradeHistoryResponse.text();
          throw new Error(
            `Failed to fetch user trade history data: ${errorText}`,
          );
        }

        setOldTrades((await userTradeHistoryResponse.json()) || []);
      } catch (error) {
        console.log(error);
      }
      try {
        const userAccountResponse = await fetch(
          `${process.env.REACT_APP_API_BASE_URL}/user-account?userId=${userId}`,
        );
        if (!userAccountResponse.ok) {
          const errorText = await userAccountResponse.text();
          throw new Error(`Failed to fetch user account data: ${errorText}`);
        }

        const userAccount = await userAccountResponse.json();

        setTotalBalance(userAccount.balance || 0);
      } catch (error) {
        console.log(error);
      }
    };

    fetchData();
  }, [currentUser, updateCounter]);

  const handleUpdateBalance = async () => {
    if (!currentUser) {
      console.error("User not authenticated");
      return;
    }

    try {
      const userId = currentUser.uid; // Use Firebase user Id
      const response = await fetch(
        `${process.env.REACT_APP_API_BASE_URL}/update-user-balance`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            userId,
            moneyAddedToBalance: 100,
          }),
        },
      );
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to update user balance: ${errorText}`);
      }
      setUpdateCounter((prev) => prev + 1);
    } catch (error) {
      console.error("Error updating balance:", error);
    }
  };

  return (
    <Stack height="100%" width="100%" flexDirection="row">
      <NavBar currentPage="my-trades" />
      <Stack padding="2rem" width="100%">
        <Typography variant="h4" marginBottom="2rem">
          My Trades
        </Typography>
        <BalanceSummary
          totalBalance={Number.isFinite(totalBalance) ? totalBalance : 0}
        />
        <Button
          variant="contained"
          color="primary"
          onClick={handleUpdateBalance}
          style={{ marginTop: "1rem" }}
        >
          + £100 to balance
        </Button>
        <Typography variant="h5" marginTop="2rem">
          Current Trades
        </Typography>
        {currentTrades.length > 0 ? (
          <TradeList trades={currentTrades} />
        ) : (
          <Typography variant="body1" color="textSecondary">
            No current trades.
          </Typography>
        )}
        <Typography variant="h5" marginTop="2rem">
          Trade History
        </Typography>
        {oldTrades.length > 0 ? (
          <TradeList trades={oldTrades} />
        ) : (
          <Typography variant="body1" color="textSecondary">
            No old trades.
          </Typography>
        )}
      </Stack>
    </Stack>
  );
}

export default MyTrades;

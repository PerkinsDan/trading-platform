import { Button } from "@mui/material";
import BalanceSummary from "./BalanceSummary";
import { useAuth } from "../context/AuthContext";
import { useEffect, useState } from "react";
import { UPDATE_USER_BALANCE_ENDPOINT, USER_ACCOUNT_ENDPOINT } from "../constants/endpoints";

const Balance = () => {
  const [totalBalance, setTotalBalance] = useState(0);
  const currentUser = useAuth();

  const fetchBalance = async () => {
    if (!currentUser) {
      console.error("User not authenticated");
      return;
    }

    const userId = currentUser.uid;

    try {
      const userAccountResponse = await fetch(USER_ACCOUNT_ENDPOINT + userId);

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

  const handleUpdateBalance = async () => {
    if (!currentUser) {
      console.error("User not authenticated");
      return;
    }

    const userId = currentUser.uid;

    if (!currentUser) {
      console.error("User not authenticated");
      return;
    }

    try {
      const response = await fetch(
        UPDATE_USER_BALANCE_ENDPOINT,
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
    } catch (error) {
      console.error("Error updating balance:", error);
    }
  };

  useEffect(() => {
    fetchBalance();
  });

  return (
    <>
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
    </>
  );
};

export default Balance;

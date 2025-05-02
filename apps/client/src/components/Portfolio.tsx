import { Stack, Typography } from "@mui/material";
import { useAuth } from "../context/AuthContext";
import { useEffect, useState } from "react";
import { Snapshot, Stock, Ticker } from "../../types";
import { USER_ACCOUNT_ENDPOINT } from "../constants/endpoints.ts";
import useFetchSnapshots from "../hooks/useFetchSnapshots.ts";

const totalValue = (portfolio: Stock[], snapshots: Map<Ticker, Snapshot>) => {
  return portfolio.reduce((acc, stock) => {
    const snapshot = snapshots.get(stock.ticker as Ticker);
    if (snapshot) {
      return acc + snapshot.price * stock.quantity;
    }
    return acc;
  }, 0);
};

const Portfolio = () => {
  const currentUser = useAuth();
  const snapshots = useFetchSnapshots();
  const [portfolio, setPortfolio] = useState<Stock[]>([]);
  const [totalValueOfStocks, setTotalValueOfStocks] = useState("0");

  useEffect(() => {
    if (!currentUser) {
      console.error("User not authenticated");
      return;
    }

    const fetchPortfolio = async () => {
      const response = await fetch(USER_ACCOUNT_ENDPOINT + currentUser.uid);

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to fetch data: ${errorText}`);
      }

      const json = await response.json();

      console.log("Portfolio data:", json.portfolio);

      setPortfolio(json.portfolio);
    };

    fetchPortfolio();
  }, [currentUser]);

  useEffect(() => {
    if (portfolio.length > 0 && snapshots) {
      const totalValueOfStocks = totalValue(portfolio, snapshots);
      setTotalValueOfStocks(totalValueOfStocks.toFixed(2));
    }
  }, [portfolio, snapshots]);

  return (
    <Stack>
      <Typography variant="h5" fontWeight={600} sx={{ mb: "1rem" }}>
        Total Value: {"$" + totalValueOfStocks}
      </Typography>
      <Stack spacing="1rem">
        <Stack
          direction="row"
          justifyContent="space-between"
          sx={{ px: "0.5rem" }}
        >
          <Typography variant="subtitle1" fontWeight={600}>
            Stock
          </Typography>
          <Typography variant="subtitle1" fontWeight={600}>
            Quantity
          </Typography>
          <Typography variant="subtitle1" fontWeight={600}>
            Holding Value
          </Typography>
        </Stack>

        {portfolio.length === 0 && (
          <Typography color="textSecondary">No stocks owned.</Typography>
        )}

        {portfolio.map((s) => {
          const snapshot = snapshots?.get(s.ticker as Ticker); // Get the snapshot for the ticker
          const currentValue = snapshot
            ? (snapshot.price * s.quantity).toFixed(2)
            : "N/A";

          return (
            <Stack
              key={s.ticker}
              direction="row"
              justifyContent="space-between"
              sx={{
                background: "#f5f5f5",
                borderRadius: "0.5rem",
                p: "0.5rem",
                boxShadow: 1,
              }}
            >
              <Typography>{s.ticker}</Typography>
              <Typography>{s.quantity}</Typography>
              <Typography>${currentValue}</Typography>
            </Stack>
          );
        })}
      </Stack>
    </Stack>
  );
};

export default Portfolio;

import { Grid, Stack } from "@mui/material";
import TradingCard from "../components/TradingCard";
import { useEffect, useState } from "react";
import { LATEST_SNAPSHOT_ENDPOINT } from "../constants/endpoints";
import { Snapshot } from "../../types";

const tickers = ["AAPL", "MSFT", "GOOGL", "AMZN", "TSLA", "META"];

function MakeTrades() {
  const [marketSnapshots, setMarketSnapshots] = useState<Snapshot[]>([]);

  useEffect(() => {
    const getTickerData = async () => {
      const responses = await Promise.all(
        tickers.map((ticker) => {
          return fetch(LATEST_SNAPSHOT_ENDPOINT + ticker);
        }),
      );

      const data = await Promise.all(
        responses.map((response) => response.json()),
      );

      setMarketSnapshots(data);
    };

    getTickerData();
  }, []);

  if (marketSnapshots)
    return (
      <Stack>
        <Grid
          gap="4rem"
          container
          sx={{
            justifyContent: "center",
            alignItems: "center",
          }}
        >
          {tickers.map((stock, index) => (
            <TradingCard
              key={stock}
              stock={stock}
              //   tradeDetails={tradeDetails}
              //   setTradeDetails={setTradeDetails}
              snapshot={marketSnapshots[index]}
            />
          ))}
        </Grid>
      </Stack>
    );

  return null;
}

export default MakeTrades;

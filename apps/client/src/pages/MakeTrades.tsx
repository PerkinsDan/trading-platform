import { Grid, Stack } from "@mui/material";
import TradingCard from "../components/TradingCard";
import useFetchSnapshots from "../hooks/useFetchSnapshots";
import { Ticker } from "../../types";

function MakeTrades() {
  const marketSnapshots = useFetchSnapshots();

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
          {Object.values(Ticker).map((stock) => {
            return (
              <TradingCard
                key={stock}
                stock={stock}
                snapshot={marketSnapshots.get(stock)}
              />
            );
          })}
        </Grid>
      </Stack>
    );

  return null;
}

export default MakeTrades;

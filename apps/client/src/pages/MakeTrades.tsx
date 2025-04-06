import { Grid, Stack } from "@mui/material";
import NavBar from "../components/NavBar";
import TradingCard from "../components/TradingCard";
import { useState } from "react";

function MakeTrades() {
  const [expandedCard, setExpandedCard] = useState<string | null>(null);

  return (
    <Stack height="100%" width="100%" flexDirection="row">
      <NavBar currentPage="make-trades" />
      <Grid
        display="grid"
        gridTemplateColumns={"repeat(2, 1fr)"}
        gap="4rem"
        padding="4rem"
        width="100%"
      >
        {[
          "Amazon",
          "Google",
          "Netflix",
          "Blockbuster",
          "Meta",
          "Dan Perkins Ltd.",
        ].map((stock) => (
          <TradingCard
            key={stock}
            stock={stock}
            isExpanded={expandedCard === stock}
            onExpand={() =>
              setExpandedCard(expandedCard === stock ? null : stock)
            }
          />
        ))}
      </Grid>
    </Stack>
  );
}

export default MakeTrades;

import { Stack } from "@mui/material";
import NavBar from "../components/NavBar";
import TradingCard from "../components/TradingCard";
import { useState } from "react";

function MakeTrades() {
  const [expandedCard, setExpandedCard] = useState<string | null>(null);

  return (
    <Stack height="100%" width="100%" flexDirection="row">
      <NavBar currentPage="make-trades" />
      <Stack
        display="grid"
        gridTemplateColumns="repeat(3, 1fr)" // Define a 3-column grid
        gridTemplateRows="auto"
        gap="4rem"
        padding="6rem" // Increase padding to add spacing from the sides
        width="100%"
        position="relative" // Allow absolute positioning of cards
      >
        {[
          "Amazon",
          "Google",
          "Netflix",
          "Blockbuster",
          "Meta",
          "Dan Perkins Ltd.",
        ].map((stock, index) => (
          <TradingCard
            key={stock}
            stock={stock}
            isExpanded={expandedCard === stock}
            onExpand={() =>
              setExpandedCard(expandedCard === stock ? null : stock)
            }
            expandedCard={expandedCard}
            index={index} // Pass the index for positioning
          />
        ))}
      </Stack>
    </Stack>
  );
}

export default MakeTrades;

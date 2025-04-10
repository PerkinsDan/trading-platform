import { Stack } from "@mui/material";
import NavBar from "../components/NavBar";
import TradingCard from "../components/TradingCard";
import { useEffect, useState } from "react";
import { auth } from "../firebaseConfig/firebase";

const tickers = ["AAPL", "MSFT", "GOOGL", "AMZN", "TSLA", "META"];
function MakeTrades() {
  const [expandedCard, setExpandedCard] = useState<string | null>(null);
  const [tradeDetails, setTradeDetails] = useState({
    type: "BUY",
    quantity: 0,
    price: 0,
  });
  const [marketSnapshots, setMarketSnapshots] = useState();

  const handleTrade = async (stock: string) => {
    try {
      const response = await fetch(
        `${process.env.REACT_APP_API_BASE_URL}/create-order`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            ticker: stock,
            type: tradeDetails.type,
            quantity: tradeDetails.quantity,
            price: tradeDetails.price,
            userId: auth.currentUser?.uid,
          }),
        },
      );
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to make trade: ${errorText}`);
      }
      alert("Trade successful!");
    } catch (error) {
      console.error("Error making trade:", error);
    }
  };

  useEffect(() => {
    const getTickerData = async () => {
      const responses = await Promise.all(
        tickers.map((ticker) => {
          return fetch(
            `${process.env.REACT_APP_MARKET_DATA_BASE_URL}/latest-snapshot/${ticker}`,
          );
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
      <Stack height="100%" width="100%" flexDirection="row">
        <NavBar currentPage="make-trades" />
        <Stack
          display="grid"
          gridTemplateColumns="repeat(3, 1fr)"
          gridTemplateRows="auto"
          gap="4rem"
          padding="6rem"
          width="100%"
          position="relative"
        >
          {tickers.map((stock, index) => (
            <TradingCard
              key={stock}
              stock={stock}
              isExpanded={expandedCard === stock}
              onExpand={() =>
                setExpandedCard(expandedCard === stock ? null : stock)
              }
              expandedCard={expandedCard}
              index={index}
              tradeDetails={tradeDetails}
              setTradeDetails={setTradeDetails}
              onTrade={() => handleTrade(stock)}
              onClose={() => setExpandedCard(null)}
              snapshot={marketSnapshots[index]}
            />
          ))}
        </Stack>
      </Stack>
    );

  return null;
}

export default MakeTrades;

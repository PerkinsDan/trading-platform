import { Stack, Typography } from "@mui/material";
import { useNavigate } from "react-router";
import { Snapshot } from "../../types";
import { useEffect, useState } from "react";
import { TIME_SERIES_ENDPOINT } from "../constants/endpoints";
import TradeGraph from "./TradeGraph";

interface TradingCardProps {
  stock: string;
  snapshot?: Snapshot;
}

export const fetchTimeSeries = async (
  stock: string,
  setTimeSeries: React.Dispatch<React.SetStateAction<number[]>>,
  setTimestamps: React.Dispatch<React.SetStateAction<string[]>>,
) => {
  try {
    const response = await fetch(TIME_SERIES_ENDPOINT + stock);
    if (!response.ok) throw new Error("Failed to fetch time series data");

    const data = await response.json();
    setTimeSeries(data.map((point: { price: number }) => point.price));
    setTimestamps(
      data.map((point: { timestamp: [number, number] }) => {
        const [hour, minute] = point.timestamp;
        return `${hour.toString().padStart(2, "0")}:${minute
          .toString()
          .padStart(2, "0")}`; // Format as HH:MM
      }),
    );
  } catch (error) {
    console.error("Error fetching time series data:", error);
  }
};

function TradingCard({ stock, snapshot }: TradingCardProps) {
  const navigate = useNavigate();

  const [timeSeries, setTimeSeries] = useState<number[]>([]);
  const [timestamps, setTimestamps] = useState<string[]>([]);

  useEffect(() => {
    fetchTimeSeries(stock, setTimeSeries, setTimestamps);
  }, [stock]);

  return (
    <Stack
      width={"20em"}
      height={"20em"}
      sx={{
        backgroundColor: "#5533ff22",
        borderRadius: "2rem",
        boxShadow: 10,
        transition: "all 0.5s ease",
      }}
      onClick={() => navigate("/make-trades/" + stock)}
    >
      <Stack
        flexDirection="row"
        justifyContent="space-between"
        alignItems="center"
        padding="1rem"
      >
        <Typography variant="h5">{stock}</Typography>
        {snapshot && <Typography>${snapshot.price}</Typography>}
      </Stack>
      <TradeGraph
        timeSeries={timeSeries}
        timestamps={timestamps}
        stock={stock}
      />
    </Stack>
  );
}

export default TradingCard;

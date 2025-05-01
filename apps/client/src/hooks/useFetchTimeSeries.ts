import { useEffect, useState } from "react";
import { Ticker } from "../../types";
import { TIME_SERIES_ENDPOINT } from "../constants/endpoints";

const useFetchTimeSeries = (stock: Ticker) => {
  const [timeSeries, setTimeSeries] = useState<number[]>([]);
  const [timestamps, setTimestamps] = useState<string[]>([]);

  useEffect(() => {
    const fetchTimeSeries = async (stock: Ticker) => {
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

    fetchTimeSeries(stock);
  }, [stock]);

  return { timeSeries, timestamps };
};

export default useFetchTimeSeries;

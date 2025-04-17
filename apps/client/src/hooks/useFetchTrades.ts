import { useEffect, useState } from "react";
import { Trade } from "../../types";
import { useAuth } from "../context/AuthContext";

const useFetchTrades = (endpoint: string) => {
  const [trades, setTrades] = useState<Trade[]>([]);
  const currentUser = useAuth();

  useEffect(() => {
    const fetchTrades = async () => {
      if (!currentUser) {
        console.error("User not authenticated");
        return;
      }

      const userId = currentUser.uid;

      try {
        const response = await fetch(endpoint + userId);

        if (!response.ok) {
          const errorText = await response.text();
          throw new Error(`Failed to fetch data: ${errorText}`);
        }

        setTrades((await response.json()) || []);
      } catch (error) {
        console.error(error);
      }
    };

    fetchTrades();
  }, [currentUser, endpoint]);

  return trades;
};

export default useFetchTrades;

import {
  Button,
  IconButton,
  MenuItem,
  Select,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import { useNavigate, useParams } from "react-router";
import CloseIcon from "@mui/icons-material/Close";
import { useEffect, useState } from "react";
import {
  CREATE_ORDER_ENDPOINT,
  LATEST_SNAPSHOT_ENDPOINT,
} from "../constants/endpoints";
import { auth } from "../firebaseConfig/firebase";
import { Snapshot, Ticker } from "../../types";
import TradeGraph from "../components/TradeGraph";
import useFetchTimeSeries from "../hooks/useFetchTimeSeries";

enum Direction {
  BUY = "BUY",
  SELL = "SELL",
}

interface TradeDetails {
  ticker: Ticker | null;
  type?: Direction;
  quantity: number;
  price: number;
  userId: string | null;
}

const TickerTrades = () => {
  const navigate = useNavigate();
  const params = useParams();

  const ticker = params.ticker as unknown as Ticker;

  const { timeSeries, timestamps } = useFetchTimeSeries(ticker);

  const [marketSnapshot, setMarketSnapshot] = useState<Snapshot | null>(null);
  const [tradeDetails, setTradeDetails] = useState<TradeDetails>({
    ticker: null,
    type: undefined,
    quantity: 0,
    price: 0,
    userId: auth?.currentUser?.uid || null,
  });
  const [error, setError] = useState<string | null>(null);

  const handleTrade = async (stock: Ticker) => {
    if (!tradeDetails.type) {
      setError("Please select a trade type (Buy or Sell).");
      return;
    }
    if (tradeDetails.quantity <= 0) {
      setError("Quantity must be greater than 0.");
      return;
    }
    if (tradeDetails.price <= 0) {
      setError("Price must be greater than 0.");
      return;
    }

    setError(null);

    try {
      const response = await fetch(CREATE_ORDER_ENDPOINT, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          ticker: stock,
          type: tradeDetails.type,
          quantity: tradeDetails.quantity,
          price: tradeDetails.price,
          userId: tradeDetails.userId,
        }),
      });
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
      try {
        const snapshot = await fetch(LATEST_SNAPSHOT_ENDPOINT + ticker);
        setMarketSnapshot(await snapshot.json());
      } catch (error) {
        console.error("Error fetching ticker data:", error);
      }
    };

    if (ticker) {
      getTickerData();
    }
  }, [ticker]);

  return (
    <Stack
      sx={{
        backgroundColor: "#5533ff22",
        borderRadius: "2rem",
        boxShadow: 10,
        maxHeight: "90vh",
      }}
    >
      <Stack
        flexDirection="row"
        justifyContent="space-between"
        alignItems="center"
        padding="1rem"
      >
        <Typography variant="h5">{ticker}</Typography>
        {marketSnapshot && <Typography>${marketSnapshot.price}</Typography>}
        <IconButton
          onClick={() => {
            navigate("/make-trades");
          }}
        >
          <CloseIcon />
        </IconButton>
      </Stack>
      <Stack maxHeight={"10%"} overflow={"hidden"}>
        <TradeGraph
          stock={ticker}
          timeSeries={timeSeries}
          timestamps={timestamps}
        />
      </Stack>
      <Stack spacing={2} padding="1rem" flex={1} overflow="auto">
        {error && (
          <Typography color="error" variant="body2">
            {error}
          </Typography>
        )}
        <Select
          value={tradeDetails.type || ""}
          onChange={(e) =>
            setTradeDetails((prev) => ({
              ...prev,
              type: e.target.value as Direction,
            }))
          }
          displayEmpty
          required
        >
          <MenuItem value="" disabled>
            Trade Type
          </MenuItem>
          <MenuItem value={Direction.BUY}>Buy</MenuItem>
          <MenuItem value={Direction.SELL}>Sell</MenuItem>
        </Select>
        <TextField
          label="Quantity"
          type="number"
          value={tradeDetails.quantity}
          onChange={(e) =>
            setTradeDetails((prev) => ({
              ...prev,
              quantity: parseInt(e.target.value, 10) || 0,
            }))
          }
          required
        />
        <TextField
          label="Price"
          type="number"
          value={tradeDetails.price}
          onChange={(e) =>
            setTradeDetails((prev) => ({
              ...prev,
              price: parseFloat(e.target.value) || 0,
            }))
          }
          required
        />
        <Button
          variant="contained"
          color="primary"
          onClick={() => handleTrade(ticker)}
        >
          Make Trade
        </Button>
      </Stack>
    </Stack>
  );
};

export default TickerTrades;

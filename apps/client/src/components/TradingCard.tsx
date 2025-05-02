import { Stack, Typography } from "@mui/material";
import { useNavigate } from "react-router";
import { Snapshot, Ticker } from "../../types";
import TradeGraph from "./TradeGraph";
import useFetchTimeSeries from "../hooks/useFetchTimeSeries";

interface TradingCardProps {
  stock: Ticker;
  snapshot?: Snapshot;
}

function TradingCard({ stock, snapshot }: TradingCardProps) {
  const navigate = useNavigate();

  const { timeSeries, timestamps } = useFetchTimeSeries(stock);

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

import { Stack, Typography } from "@mui/material";
import ArrowDropUpIcon from "@mui/icons-material/ArrowDropUp";
import ArrowDropDownIcon from "@mui/icons-material/ArrowDropDown";

interface Trade {
  ticker: string;
  quantity: number;
  price: number;
  type: string;
}

interface TradeListProps {
  trades: Trade[];
}

function TradeList({ trades }: TradeListProps) {
  return (
    <Stack spacing="1rem">
      {trades.map((trade, index) => (
        <Stack
          key={index}
          direction="row"
          justifyContent="space-between"
          padding="1rem"
          sx={{
            backgroundColor: "#f5f5f5",
            borderRadius: "0.5rem",
            boxShadow: 1,
          }}
        >
          <Typography>{trade.ticker}</Typography>
          <Stack direction="row">
            <Typography>
              {trade.quantity} @ ${trade.price.toFixed(2)}
            </Typography>
            {trade.type == "BUY" ? (
              <Stack direction={"row"} style={{ color: "green" }}>
                {" "}
                <ArrowDropUpIcon />
                <Typography>BUY</Typography>
              </Stack>
            ) : (
              <Stack direction={"row"} style={{ color: "red" }}>
                <ArrowDropDownIcon />
                <Typography>SELL</Typography>
              </Stack>
            )}
          </Stack>
        </Stack>
      ))}
    </Stack>
  );
}

export default TradeList;

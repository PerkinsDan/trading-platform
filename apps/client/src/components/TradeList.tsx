
import { Stack, Typography } from "@mui/material";

interface Trade {
  ticker: string;
  quantity: number;
  price: number;
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
          <Typography>
            {trade.quantity} @ ${trade.price.toFixed(2)}
          </Typography>
        </Stack>
      ))}
    </Stack>
  );
}

export default TradeList;
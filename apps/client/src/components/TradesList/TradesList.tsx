import {Button, Stack, Typography} from "@mui/material";
import ArrowDropUpIcon from "@mui/icons-material/ArrowDropUp";
import ArrowDropDownIcon from "@mui/icons-material/ArrowDropDown";
import {Trade} from "../../../types";

interface TradeListProps {
  trades: Trade[];
  onCancel?: (trade: Trade) => void;
}

function TradeList({ trades, onCancel }: TradeListProps) {
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
          <Stack direction="row" spacing={2} alignItems="center">
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
            {onCancel && (
                <Button
                    size="small"
                    variant="outlined"
                    color="error"
                    onClick={() => onCancel(trade)}
                >
                  Cancel
                </Button>
            )}
          </Stack>
        </Stack>
      ))}
    </Stack>
  );
}

export default TradeList;

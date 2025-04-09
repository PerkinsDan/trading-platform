import {
  Stack,
  Typography,
  Button,
  TextField,
  Select,
  MenuItem,
  IconButton,
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";

interface TradingCardProps {
  stock: string;
  isExpanded: boolean;
  onExpand: () => void;
  expandedCard: string | null;
  index: number;
  tradeDetails: { type: string; quantity: number; price: number };
  setTradeDetails: React.Dispatch<
    React.SetStateAction<{ type: string; quantity: number; price: number }>
  >;
  onTrade: () => void;
  onClose: () => void; // Add onClose prop
}

function TradingCard({
  stock,
  isExpanded,
  onExpand,
  expandedCard,
  index,
  tradeDetails,
  setTradeDetails,
  onTrade,
  onClose,
}: TradingCardProps) {
  const row = Math.floor(index / 3) + 1;
  const col = (index % 3) + 1;

  return (
    <Stack
      position="absolute"
      top={isExpanded ? "6rem" : `calc(${row - 1} * (calc(50% - 2rem)) + 4rem)`}
      left={
        isExpanded ? "6rem" : `calc(${col - 1} * (calc(33.33% - 2rem)) + 4rem)`
      }
      width={isExpanded ? "calc(100% - 12rem)" : "calc(33.33% - 6rem)"}
      height={isExpanded ? "calc(100% - 12rem)" : "calc(50% - 6rem)"}
      sx={{
        backgroundColor: "#5533ff22",
        borderRadius: "2rem",
        boxShadow: 10,
        transition: "all 0.5s ease",
        opacity: expandedCard && !isExpanded ? 0 : 1,
        pointerEvents: expandedCard && !isExpanded ? "none" : "auto",
        "&:hover": {
          transform: isExpanded ? "none" : "scale(1.05)",
          cursor: isExpanded ? "default" : "pointer",
        },
      }}
      onClick={!isExpanded ? onExpand : undefined} // Disable click when expanded
    >
      <Stack
        flexDirection="row"
        justifyContent="space-between"
        alignItems="center"
        padding="1rem"
      >
        <Typography variant="h5">{stock}</Typography>
        {isExpanded && (
          <IconButton
            onClick={(e) => {
              e.stopPropagation();
              onClose();
            }}
          >
            <CloseIcon />
          </IconButton>
        )}
      </Stack>
      {isExpanded && (
        <Stack spacing={2} padding="1rem">
          <Select
            value={tradeDetails.type}
            onChange={(e) =>
              setTradeDetails((prev) => ({ ...prev, type: e.target.value }))
            }
          >
            <MenuItem value="BUY">Buy</MenuItem>
            <MenuItem value="SELL">Sell</MenuItem>
          </Select>
          <TextField
            label="Quantity"
            type="number"
            value={tradeDetails.quantity}
            onChange={(e) =>
              setTradeDetails((prev) => ({
                ...prev,
                quantity: parseInt(e.target.value, 10),
              }))
            }
          />
          <TextField
            label="Price"
            type="number"
            value={tradeDetails.price}
            onChange={(e) =>
              setTradeDetails((prev) => ({
                ...prev,
                price: parseFloat(e.target.value),
              }))
            }
          />
          <Button variant="contained" color="primary" onClick={onTrade}>
            Make Trade
          </Button>
        </Stack>
      )}
    </Stack>
  );
}

export default TradingCard;

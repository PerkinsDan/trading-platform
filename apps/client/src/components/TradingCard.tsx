import { Stack, Typography } from "@mui/material";

interface TradingCardProps {
  stock: string;
  isExpanded: boolean;
  onExpand: () => void;
}

function TradingCard({ stock, isExpanded, onExpand }: TradingCardProps) {
  return (
    <Stack
      width={isExpanded ? "100%" : "auto"}
      height={isExpanded ? "100%" : "auto"}
      gridColumn={isExpanded ? "span 2" : "auto"}
      sx={{
        backgroundColor: "#5533ff22",
        borderRadius: "2rem",
        boxShadow: 10,
        transition: "all 0.2s",
        "&:hover": {
          transform: isExpanded ? "none" : "scale(1.05)",
          cursor: "pointer",
        },
      }}
      onClick={onExpand}
    >
      <Stack
        flexDirection="row"
        justifyContent="space-between"
        alignItems="center"
        padding="1rem"
      >
        <Typography variant="h5">{stock}</Typography>
      </Stack>
      <Stack
        flexDirection="column"
        justifyContent="space-between"
        alignItems="center"
        height="100%"
      >
        <Typography variant="body1">Card Content</Typography>
      </Stack>
    </Stack>
  );
}

export default TradingCard;

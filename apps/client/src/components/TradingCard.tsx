import { Stack, Typography } from "@mui/material";

interface TradingCardProps {
  stock: string;
  isExpanded: boolean;
  onExpand: () => void;
  expandedCard: string | null;
  index: number;
}

function TradingCard({ stock, isExpanded, onExpand, expandedCard, index }: TradingCardProps) {
  const row = Math.floor(index / 3) + 1; 
  const col = (index % 3) + 1; 

  return (
    <Stack
      position="absolute" 
      top={isExpanded ? "6rem" : `calc(${row - 1} * (calc(50% - 2rem)) + 4rem)`}
      left={isExpanded ? "6rem" : `calc(${col - 1} * (calc(33.33% - 2rem)) + 4rem)`}
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

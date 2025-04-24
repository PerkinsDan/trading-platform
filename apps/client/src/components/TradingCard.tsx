import { Stack, Typography } from "@mui/material";
import { useNavigate } from "react-router";
import { Snapshot } from "../../types";

interface TradingCardProps {
  stock: string;
  snapshot?: Snapshot;
}

function TradingCard({ stock, snapshot }: TradingCardProps) {
  const navigate = useNavigate();

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
      onClick={() => {
        console.log("Clicked on stock: ", stock);
        navigate("/make-trades/" + stock);
      }}
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
    </Stack>
  );
}

export default TradingCard;

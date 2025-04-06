
import { Stack, Typography } from "@mui/material";

interface BalanceSummaryProps {
  totalBalance: number;
  lockedFunds: number;
}

function BalanceSummary({ totalBalance, lockedFunds }: BalanceSummaryProps) {
  return (
    <Stack
      direction="row"
      justifyContent="space-between"
      padding="1rem"
      sx={{
        backgroundColor: "#e0f7fa",
        borderRadius: "0.5rem",
        boxShadow: 1,
      }}
    >
      <Typography>Total Balance: ${totalBalance.toFixed(2)}</Typography>
      <Typography>
        Including Locked Funds: ${(totalBalance + lockedFunds).toFixed(2)}
      </Typography>
    </Stack>
  );
}

export default BalanceSummary;
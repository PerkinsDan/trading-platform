import { Stack, Typography } from "@mui/material";
import Portfolio from "../components/Portfolio";

function MyPortfolio() {
  return (
    <Stack>
      <Typography variant="h4" marginBottom="2rem">
        My Portfolio
      </Typography>
      <Portfolio />
    </Stack>
  );
}

export default MyPortfolio;

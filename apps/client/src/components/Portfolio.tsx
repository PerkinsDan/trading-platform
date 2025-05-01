import {Stack, Typography} from "@mui/material";
import {useAuth} from "../context/AuthContext";
import usePortfolio from "../hooks/usePortfolio";

const Portfolio = () => {
    const stocks = usePortfolio();
    const currentUser = useAuth();

    if (!currentUser) {
        console.error("User not authenticated");
        return;
    }

    return (
    <Stack spacing="1rem">

        <Stack
            direction="row"
            justifyContent="space-between"
            sx={{ px: "0.5rem" }}
        >
            <Typography variant="subtitle1" fontWeight={600}>
                Stock
            </Typography>
            <Typography variant="subtitle1" fontWeight={600}>
                Quantity
            </Typography>
        </Stack>

        {stocks.length === 0 && (
            <Typography color="textSecondary">No stocks owned.</Typography>
        )}

        {stocks.map(s => (
            <Stack
                key={s.ticker}
                direction="row"
                justifyContent="space-between"
                sx={{
                    background: "#f5f5f5",
                    borderRadius: "0.5rem",
                    p: "0.5rem",
                    boxShadow: 1,
                }}
            >
                <Typography>{s.ticker}</Typography>
                <Typography>{s.quantity}</Typography>
            </Stack>
        ))}
    </Stack>


    );
};

export default Portfolio;

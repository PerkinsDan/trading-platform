import {Stack, Typography} from "@mui/material";
import {useAuth} from "../context/AuthContext";
import {useEffect, useState} from "react";
import {Stock} from "../../types";
import {USER_ACCOUNT_ENDPOINT} from "../constants/endpoints.ts";

const Portfolio = () => {
    const stocks = usePortfolio();
    const currentUser = useAuth();
    const [portfolio, setPortfolio] = useState<Stock[]>([]);

    function usePortfolio(reload = 0) {
        useEffect(() => {

            if (!currentUser) {
                console.error("User not authenticated");
                return;
            }

            (async () => {
                const response = await fetch(USER_ACCOUNT_ENDPOINT + currentUser.uid);

                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(`Failed to fetch data: ${errorText}`);
                }

                const json = await response.json();
                setPortfolio(json.portfolio ?? []);
            })();
        }, [currentUser, reload]);

        return portfolio;
    }

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

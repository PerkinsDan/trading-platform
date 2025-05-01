import {useEffect, useState} from "react";
import {Stock} from "../../types";
import {useAuth} from "../context/AuthContext";
import {USER_ACCOUNT_ENDPOINT} from "../constants/endpoints";

export default function usePortfolio(reload = 0) {
    const [portfolio, setPortfolio] = useState<Stock[]>([]);
    const currentUser = useAuth();

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

const ORDER_PROCESSOR_BASE_URL = process.env.REACT_APP_API_BASE_URL;

export const TRADES_PENDING_ENDPOINT = `${ORDER_PROCESSOR_BASE_URL}/user-active-positions?userId=`;
export const TRADES_HISTORY_ENDPOINT = `${ORDER_PROCESSOR_BASE_URL}/user-trade-history?userId=`;
export const USER_ACCOUNT_ENDPOINT = `${ORDER_PROCESSOR_BASE_URL}/user-account?userId=`;

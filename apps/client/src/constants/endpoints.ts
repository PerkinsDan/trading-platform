const ORDER_PROCESSOR_BASE_URL = import.meta.env
  .VITE_APP_ORDER_PROCESSOR_BASE_URL;

export const TRADES_PENDING_ENDPOINT = `${ORDER_PROCESSOR_BASE_URL}/user-active-positions?userId=`;
export const TRADES_HISTORY_ENDPOINT = `${ORDER_PROCESSOR_BASE_URL}/user-trade-history?userId=`;
export const USER_ACCOUNT_ENDPOINT = `${ORDER_PROCESSOR_BASE_URL}/user-account?userId=`;
export const CREATE_ORDER_ENDPOINT = `${ORDER_PROCESSOR_BASE_URL}/create-order`;
export const CANCEL_ORDER_ENDPOINT = `${ORDER_PROCESSOR_BASE_URL}/cancel-order`;

const MARKET_DATA_BASE_URL = import.meta.env.VITE_APP_MARKET_DATA_BASE_URL;

export const LATEST_SNAPSHOT_ENDPOINT = `${MARKET_DATA_BASE_URL}/latest-snapshot/`;

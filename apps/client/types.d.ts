export interface Trade {
  orderId: string;
  ticker: string;
  quantity: number;
  price: number;
  type: string;
}

export interface Snapshot {
  price: number;
  timestamp: [number, number];
  change: number;
}

export enum Ticker {
  AAPL,
  MSFT,
  GOOGL,
  AMZN,
  TSLA,
  META,
}

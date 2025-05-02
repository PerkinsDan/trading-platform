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
  AAPL = "AAPL",
  MSFT = "MSFT",
  GOOGL = "GOOGL",
  AMZN = "AMZN",
  TSLA = "TSLA",
  META = "META",
}

export interface Stock {
  ticker: Ticker;
  quantity: number;
}

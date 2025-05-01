# Market Data Service

**Filename**: `MarketDataService.java`

## What does it do?

- `MarketDataService` is the top-level class of the MarketData microservice and acts as the interface to the MarketData API.
- On instantiation:
  - If the app is starting up, it generates a day's worth of data.
  - Otherwise, it checks every hour if the time is 30 minutes either side of `PRE_MARKET_OPEN` and generates another day's worth of data if `True`
- The `MarketDataService` runs with a Daemon thread, as the main thread is reserved for handling API calls.
- It is created as a Singleton for thread-safety and global access control, ensuring all market data comes from the same source.

## When do we use it?

- Anytime we need market data, such as in the front-end `LiquidityEngine`.

## Attributes

- **`simulateData`**: Access point to the `simulateData` class and its `generateData()` method.
- **`marketDataServiceHolder`**: Singleton object for the class.

## Methods

### `MarketDataService()`

- **Description**: Creates the service object and starts the Daemon thread.

### `getDataGenerationThread()`

- **Returns**: The daemon thread which generates data daily

### `getInstance()`

- **Returns**: The single instance of `MarketDataService`.

### `getTimeSeries(Ticker)`

- **Returns**: The `TimeSeries` associated with a `Ticker` up to the current time.

### `getLatestSnapshot(Ticker)`

- **Returns**: The `Snapshot` associated with the current time.

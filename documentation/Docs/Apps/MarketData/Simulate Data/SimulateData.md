# Simulate Data

**Filename**: `simulatedata/SimulateData.java`

## What does it do?

- `SimulateData` aggregates `TimeSeries`, which in turn aggregate `Snapshots`.
- `SimulateData` contains a hashmap of `TimeSeries` objects. Each `TimeSeries` is mapped to a `Ticker` and is used to retrieve 'market data' every 5 minutes.
- We use randomly generated data because we couldn't find a free market data API that fit our requirements.
- The main functionality is in the `generateData()` method, which creates a day's worth of time series data in advance so all the data is ready before the market opens.

## When do we use it?

- Heavily used by the front end to generate time series graphs of prices for each ticker.
  - Market price is also used in the front end as a reference when placing an order.
- Used in the `LiquidityEngine` service (coming soon) as a starting price to generate orders around.

## Attributes

- **`marketOpenTime`**: Time-zone agnostic open time set at 9:30 AM.
- **`marketCloseTime`**: Time-zone agnostic close time set at 4:00 PM.
- **`timeSeriesMap`**: The hashmap containing the `TimeSeries` for each ticker.

## Methods

### `SimulateData()`

- **Constructor**: Creates the `timeSeriesMap`, which is empty on instantiation.

### `getTimeSeries(Ticker)`

- **Description**: Returns the `TimeSeries` that corresponds to the `Ticker` passed in.

### `generateData()`

- **Description**:
  - For each `Ticker` in the `timeSeriesMap`, it starts at `marketOpenTime`, generates a price, and creates a `Snapshot`.
  - Repeats this process of randomly generating a price and creating a `Snapshot` every 5 minutes until `marketCloseTime`.

# Time Series

**Filename**: `simulatedata/TimeSeries.java`

## What does it do?

The `TimeSeries` class represents a collection of time-based data snapshots.  
It provides functionality to manage and retrieve snapshots based on their timestamps.  
This class is useful for managing time-series data and ensuring that only relevant snapshots are considered based on the current time.

## When do we use it?

- Used by the `simulateData` class, which aggregates `TimeSeries` into a `timeSeriesMap`.

## Attributes

- **`snapshots`**: A list that stores instances of the `Snapshot` class. Each `Snapshot` represents a data point with an associated timestamp.

## Methods

### `TimeSeries()`

- **Description**: Constructor that initializes `snapshots`.

### `addSnapshot(Snapshot snapshot)`

- **Description**: Adds a new `Snapshot` to `snapshots`.

### `getSnapshots()`

- **Description**:
  - Returns a filtered list of `Snapshot` instances whose timestamps are not in the future.
  - Ensures that only valid snapshots up to the current time are returned.

### `getLatestSnapshot()`

- **Description**:
  - Retrieves the most recent `Snapshot` from `snapshots` whose timestamp is before the current time.
  - Returns `null` if no valid snapshots exist.

# Snapshot

**Filename**: `Snapshot.java`

## What does it do?

- `Snapshot` is a record class. A record is a special type of class designed to be an immutable data carrier.
- Once a `Snapshot` is created, it cannot be changed.

## Where do we use it?

- `Snapshot` is the base unit for market data in this application.
- It holds a timestamp, a price for a specific moment in time, and the change from the previous price.

## Attributes

- **`price`**: The price of the stock at a particular moment in time.
- **`timestamp`**: The timestamp specifying the moment in time the price is for.
- **`change`**: The change in price from the previous `Snapshot`.

## Methods

- Records have public 'getters' named after the attributes themselves, rather than following the `getAttribute()` convention.
  - **`price()`**: Returns the price of the `Snapshot`.
  - **`timestamp()`**: Returns the timestamp of the `Snapshot`.
  - **`change()`**: Returns the change in price of the `Snapshot`.

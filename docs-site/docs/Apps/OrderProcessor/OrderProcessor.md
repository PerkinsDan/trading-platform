# OrderProcessor

**Filename**: `OrderProcessor.java`

## What does it do?

- `OrderProcessor` is the top-level class of the OrderProcessor microservice. It stores and processes active orders and interfaces with the API.
- It is a Singleton, ensuring thread-safety and universal access control.
- `OrderProcessor` aggregates `TradeBooks`, which in turn aggregate `Orders`, and stores all of them in a map to provide the API with a single point of contact for the core functionality of the OrderProcessor service.
- It manages adding orders to relevant books, matching orders, and order cancellation.

## When do we use it?

- Since it is the top-level class of the OrderProcessor service and serves as the interface for the API, it is used any time the OrderProcessor service is called.

## Attributes

- **`orderProcessor`**: The single instance of the class.
- **`tradeBookMap`**: A `HashMap` that stores a `TradeBook` for each ticker.

## Methods

### `OrderProcessor()`

- **Description**: Private constructor to enforce the Singleton pattern.

### `getInstance()`

- **Description**: Static method to retrieve the Singleton instance of the `OrderProcessor`.

### `processOrder(Order order)`

- **Description**:
  - Composite method that adds an `Order` to the relevant `TradeBook`, evaluates it for matches, and returns a list of `MatchingDetails` objects expressed as JSON to the API.
  - The API uses this JSON to update the database.

### `cancelOrder(Order order)`

- **Description**:
  - Cancels an order by finding it in the appropriate `TradeBook` and removing it.
  - **Returns**:
    - `True` if the order was removed successfully.
    - `False` if the order could not be found, which typically means the order was already filled and removed from the books before the cancellation request was processed.

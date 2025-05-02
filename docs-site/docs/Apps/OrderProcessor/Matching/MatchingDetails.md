# MatchingDetails

**Filename**: `MatchingDetails.java`

## What is it?

The `MatchingDetails` class is used to capture information about the details of a trade match. Two `MatchingDetails` objects are generated per match: one for the buy order and one for the sell order. The `MatchingDetails` object is used to update information about orders and users in the database.

## When do we use it?

- Anytime an order is submitted, it is added to the relevant `TradeBook` where it can be matched.
- When two orders match, we create a `MatchingDetails` object for each and pass these objects (acting as DTOs) to the API, which uses them to update information in the database.

## Attributes

- **`orderID`**: A unique identifier pulled from the order that was matched, ensuring the correct order is updated.
- **`price`**: The price at which the order was matched.
- **`quantityChange`**: The quantity (number of stocks) that was matched, as not all orders are filled completely on the first match.
- **`filled`**: A boolean identifier used to determine whether an order has been filled entirely.

## Methods

### `getOrderID()`

- **Description**: Returns the `orderID` attribute.

### `getPrice()`

- **Description**: Returns the `price` attribute.

### `getQuantityChange()`

- **Description**: Returns the `quantityChange` attribute.

### `isFilled()`

- **Description**: Returns `true` or `false` depending on whether the order has been filled.

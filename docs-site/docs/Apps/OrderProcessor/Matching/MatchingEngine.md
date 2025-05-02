# MatchingEngine

**Filename**: `MatchingEngine.java`

## What is it?

The `MatchingEngine` class is a utility class filled with static methods.  
It contains the core order matching methods that are called each time an order is placed. Its role is to look at a `TradeBook` and find as many matches between orders as it can, create `MatchingDetails` DTOs for each match, and return them.

## When do we use it?

- During trading hours, this class is called every single time an order is submitted.
- On each call, it evaluates the `TradeBook`, matches orders, and generates DTOs for each match until there are no more possible matches.
- It is called exactly once per order, but each call may result in more than one match, as orders are matched in price-time priority (see `OrderComparator` for further explanation).
- An order being filled means a new order is now first in line to be matched.

## Attributes

- **`buyOrders`**: A `PriorityQueue` of buy orders.
- **`sellOrders`**: A `PriorityQueue` of sell orders.
- **`matchesFound`**: An `ArrayList` of JSON strings, where each JSON is a serialized version of a `MatchingDetails` object.

## Methods

### `match(TradeBook tradeBook)`

- **Description**:
  - Takes a `TradeBook` as input, evaluates the `TradeBook` for matches, processes them, and returns an `ArrayList` of `MatchingDetails` JSONs that are used to update the database.

### `processMatches()`

- **Description**:
  - Helper method called by the `match()` method.
  - Responsible for updating orders in the `TradeBook`.
  - Adjusts quantities, creates and serializes the `MatchingDetails` objects, appends them to the `matchesFound` list, and removes completed orders from the `TradeBook` to avoid reprocessing.

### `matchPossible(PriorityQueue buyOrders, PriorityQueue sellOrders)`

- **Description**:
  - Helper method used by the `match()` method.
  - Looks at both sides of the `TradeBook` and checks if there is exactly one match possible.
  - Returns `true` while a match is possible and `false` when no matches are possible.

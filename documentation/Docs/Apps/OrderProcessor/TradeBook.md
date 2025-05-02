# TradeBook

**Filename**: `TradeBook.java`

## What does it do?

- A `TradeBook` holds all of the active `BUY` and `SELL` orders for a particular stock.
- `BUY` and `SELL` orders are held in separate `PriorityQueue`s.
- The `PriorityQueue`s sort orders according to price-time priority automatically, meaning we don't ever have to worry about sorting the orders ourselves.  
  We can just poll `Orders` from the front of the queue and match them.

## When do we use it?

- `TradeBook` is used in the `TradeBookMap` attribute of the `OrderProcessor` class.
- It is mainly useful for keeping our `Orders` sorted correctly.
- We create the `TradeBookMap` and, by extension, the `TradeBook` objects on startup of the app and keep them in memory indefinitely.

## Attributes

- **`buyOrders`**: A `PriorityQueue` of buy orders, sorted with the highest price first.
- **`sellOrders`**: A `PriorityQueue` of sell orders, sorted with the lowest price first.

## Methods

- **`addToBook(Order order)`**: Adds an order to the relevant side (`BUY`/`SELL`) of the `TradeBook`.
- **`getBuyOrders()`**: Returns the queue of buy orders.
- **`getSellOrders()`**: Returns the queue of sell orders.

# Order

**Filename**: `Order.java`

## What is it?

An `Order` represents an intention to buy or sell a specific amount of a specific stock at a specific price.  
The `Order` class is the Data Model Object (DMO) used to represent the intent of an agent to engage in a trade.  
Each `Order` object represents one side of a trade (i.e., there is an `Order` object for both the buy and sell sides of a trade) and is treated as an individual entity rather than as part of a "trade."

## When do we use it?

- An `Order` object is created when an order is placed.
- It is persisted in the database while it is processed.
- Upon being filled, it is marked as "filled" and warehoused in case it is needed for reference or regulatory reporting.

## Attributes

- **`orderId`**: A UUID used to identify the order through its lifetime, generated on instantiation.
- **`type`**: `BUY` or `SELL`, determined by the user.
- **`price`**: The price at which the order is to be executed, determined by the user.
- **`timestamp`**: The time at which the order was submitted.
- **`ticker`**: A symbol representing which stock the user is interested in buying or selling.
- **`quantity`**: The amount of stock that the user wants to buy or sell.
- **`cancelled`**: `false` by default, only changed if a cancellation request is submitted.
- **`filled`**: `false` by default, only changed when an order has been completely filled.
- **`userId`**: Used to assign each order to a specific user.

## Methods

- **Getters**: For the various attributes.
- **`reduceQuantity(int amount)`**: Decreases the `quantity` attribute of the order.
- **`toString()`**: Useful for logging, displays attributes in dictionary format.

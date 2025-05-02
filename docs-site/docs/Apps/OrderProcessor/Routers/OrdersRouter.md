# Orders Router

**Filename**: `OrdersRouter.java`

## What does it do

`OrdersRouter` builds a dedicated Vert.x `Router` that exposes the Order API for the trading platform:

| Path             | HTTP | Description                                                                       |
| ---------------- | ---- | --------------------------------------------------------------------------------- |
| `/orders/create` | POST | Places a new order, adds it to the book, updates MongoDB collections accordingly. |
| `/orders/cancel` | POST | Cancels an existing order, credits funds or adjusts portfolio as needed.          |

Internally the router delegates many methods to `OrderProcessorService` and `DatabaseUtils`.

## Methods

### `OrdersRouter(Vertx vertx)`

- **Constructor**
  1. Creates a new `Router`.
  2. Gets the singleton `OrderProcessorService`.
  3. Calls **`initialise()`** to wire the HTTP handlers.

---

### `createOrder(JsonObject body)`

Utility factory that converts the incoming JSON payload into an `Order` domain object.

| Field in `body` | Mapped to        |
| --------------- | ---------------- |
| `type`          | `OrderType` enum |
| `ticker`        | `Ticker` enum    |
| `price`         | `double`         |
| `quantity`      | `int`            |
| `userId`        | String           |

---

### `getRouter()`

Returns the fully configured `Router` so the master router (or the HTTP server) can mount it at `/orders/*`.

---

### `initialise()`

Private bootstrap that attaches both route handlers:

#### `/create`

1. Parses the request body (`BodyHandler`).
2. Runs a composed `Validation` chain to check quantities, price, type, ticker, user ID, and funding/portfolio sufficiency.
3. On success:
   - Builds an `Order`, inserts it into **`activeOrders`**, processes matches, and (if any) calls `updateCollectionsWithMatches`.
   - Responds with **200 OK**.
4. On validation failure: **400 Bad Request**.
5. On unexpected exceptions: **500 Internal Server Error**.

#### `/cancel`

1. Parses the body and validates order identity, ownership, etc.
2. Retrieves the order document; removes it from the order book and the **`activeOrders`** collection.
3. Credits user balance if the order being cancelled was a **BUY**.
4. Marks the order as `cancelled` in **`orderHistory`** (or inserts it if it was never filled).
5. Respond codes: **200 OK**, **404** on validation failure, **500** on exceptions.

**When do we use it?**

Used by `MasterRouter`.

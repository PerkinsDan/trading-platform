# Users Router

**Filename**: `UsersRouter.java`

## What does it do

`UsersRouter` defines the User API for the trading platform.  
It lets clients create an account, top‑up their balance, and see their active positions, account status, and historical trades.

| Path                      | HTTP | Purpose                                             |
| ------------------------- | ---- | --------------------------------------------------- |
| `/users/create`           | POST | Register a new user (balance = 0, empty portfolio). |
| `/users/update-balance`   | POST | Credit funds to an existing user account.           |
| `/users/active-positions` | GET  | List all open orders for the user.                  |
| `/users/account`          | GET  | Return the user’s cash balance.                     |
| `/users/trade-history`    | GET  | Fetch the user’s order history.                     |

## Methods

### `UsersRouter(Vertx vertx)`

- **Constructor**
  1. Instantiates a new Vert.x `Router`.
  2. Calls **`setupRoutes()`** to wire all handlers.

---

### `getRouter()`

- **Returns**: the configured `Router`, mounted by `MasterRouter` at `/users/*`.

---

### `setupRoutes()`

Attaches five routes:

1. **`POST /create`**

   - Body must contain `userId`.
   - Validates that the `userId` does not already exist.
   - Inserts a new document in `users` with `balance = 0` and empty `portfolio`.
   - Responses: **200 OK**, **400** if user already exists, **500** on exceptions.

2. **`POST /update-balance`**

   - Body requires `userId` and `moneyAddedToBalance`.
   - Validates inputs, then calls `creditUser()`.
   - Responses: **200 OK** on success, **400** on validation failure, **500** on exceptions.

3. **`GET /active-positions`**

   - Query parameter `userId`.
   - Returns JSON array of the user’s documents in the `activeOrders` collection.
   - Responses: **200 OK**, **400** on validation failure, **500** on errors.

4. **`GET /account`**

   - Query parameter `userId`.
   - Retrieves the user document from `users` and returns `{ userId, balance }`.
   - Responses: **200 OK**, **204** if user not found, **400** on validation issues, **500** on errors.

5. **`GET /trade-history`**
   - Query parameter `userId`.
   - Retrieves the user’s records from `orderHistory`; returns an empty **204** if none.
   - Responses: **200 OK**, **400** on validation failure, **500** on errors.

**When do we use it?**

Used by `MasterRouter`.

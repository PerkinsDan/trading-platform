# Database Utils

Filename: `DatabaseUtils.java`

## What does it do

`DatabaseUtils` contains methods useful for manipulating our MongoDB database to reflect users creating accounts, orders being made, trades being executed and any other interaction that requires the database to be updated.

## When do we use it?

The methods contained in this class are used by the Vert.x API's endpoints.

## Methods

### `creditUser(String userId, double amountToAdd)`

- Purpose: Credits the user’s account by incrementing their `balance` field.
- Operation: Runs a MongoDB `$inc` update against the`users`collection for the document where`userId`matches.

---

### `previouslyPartiallyFilled(String orderId)`

- Purpose: Determines whether an order has already been partially filled.
- Operation: Searches the`orderHistory`collection for a document whose `orderId` attribute matches the string that is passed in.
- Returns:
  - `true`— an entry exists (the order was previously partially filled).
  - `false`— no such entry exists.

---

### `userBalanceIsSufficientForBuy(JsonObject body)`

Validates that the user has enough cash to fulfil a BUY order.

- Parameters expected in`body`:
  - `userId`(string)
  - `type`- should be a BUY order
  - `price`(double)
  - `quantity`(int)
- Logic:
  1. Confirms the order type is BUY; otherwise returns `ValidationResult.fail`.
  2. Calculates the`totalPrice = price × quantity`.
  3. Retrieves the user’s `balance` from the`users`collection.
  4. Returns:
     - `ValidationResult.ok()` if `balance≥totalPrice`.
     - `ValidationResult.fail("Insufficient funds to place this order")` if not.

---

### `userPortfolioIsSufficientForSell(JsonObject body)`

Validates that the user owns enough stock to fulfil a SELL order.

- Parameters expected in`body`:
  - `userId`(string)
  - `type`("SELL") - should be a SELL order
  - `ticker`(string) - stock symbol
  - `quantity`(int)
- Logic:
  1. Confirms the order type is SELL; otherwise returns `ValidationResult.fail`.
  2. Pulls the element of the user’s`portfolio`array whose `ticker` matches.
  3. Extracts the current `quantity` owned (defaults to`0` if absent).
  4. Returns:
     - `ValidationResult.ok()` if owned `quantity≥requested quantity`.
     - `ValidationResult.fail("Insufficient quantity of stock owned to place this order")` if not.

---

### `updateCollectionsWithMatches(ArrayList<String> matchesFound)`

Processes a sequence of executed trade matches, updating all relevant collections.

- Input: Each element in`matchesFound`is a JSON string describing a match (fields such as `orderID`, `userId`, `ticker`, `price`, `quantityChange`, `filled`).
- Flow:
  1. Converts the strings to `Document` objects via `convertMatchesToDocs`.
  2. Iterates through the list of matches, performing different operations, depending on whether it is a BUY or SELL order, via `isBuy`.
  3. For each match:
     - Locates the matching order in `activeOrders`.
     - Calculates the `netBalanceChange`.
     - BUY path:
       - Adds shares to the buyer’s portfolio (`updatePortfolio`).
       - Moves filled orders to `orderHistory`.
     - SELL path:
       - Removes shares (`updatePortfolio` with negative quantity).
       - Credits the seller’s balance (`updateBalance`).
       - Moves filled orders to `orderHistory`.

---

### `getDocByOrderId(String orderId, MongoCollection<Document> collection)`

- Purpose: Retrieves a single document from the supplied collection whose `orderId` matches.

---

### `updatePortfolio(Document match, MongoCollection<Document> usersCollection, int signedQuantityChange)`

- Purpose: Increments/decrements the user’s share count for the given `ticker`.
- Edge case: If the user doesn’t yet hold the `ticker`, pushes a brand‑new portfolio entry.

---

### `updateBalance(String userId, MongoCollection<Document> collection, double signedBalanceChange)`

- Purpose: Adjusts a user’s cash balance by the signed amount (positive=credit, negative=debit).

---

### `moveToHistory(Document order, MongoCollection<Document> orderHistory, MongoCollection<Document> activeOrders)`

- Purpose: Marks an order as `filled`, inserts it into `orderHistory`, and removes it from `activeOrders`.

---

### `convertMatchesToDocs(ArrayList<String> matchesFound)`

- Purpose: Transforms raw JSON strings representing matches into `Document` objects for internal processing.

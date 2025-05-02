# ValidationBuilder

**Package**: `com.tradingplatform.orderprocessor.validations`  
**Class**: `ValidationBuilder`

The `ValidationBuilder` class implements a builder pattern to construct a chain of validations for order-related operations. It supports checks for field presence, format, correctness, and referential integrity with the database.

---

## Fields

### `private final List<Validation> validations`
- A list that holds the sequence of validation lambdas.
- Each lambda takes a `JsonObject` (the order request) and returns a `ValidationResult`.

### `private static final Set<String> ValidOrderTypes`
- A set of all valid order types, extracted from the `OrderType` enum (e.g., `BUY`, `SELL`).

### `private static final Set<String> ValidTickers`
- A set of all valid ticker symbols from the `Ticker` enum.

---

## Private Methods

### `private Boolean missingOrEmpty(JsonObject body, String attribute)`
Checks whether the given attribute:
- Is absent from the JSON body.
- Or is present but has a blank value.
- Common check to all endpoints, so extracted as a helper method.

#### Returns
- `true` if missing or blank.
- `false` otherwise.

---

## Public Methods

### `public ValidationBuilder validateDouble(String attribute)`
Validates that a specified attribute:
- Is present and not blank.
- Can be parsed as a `double`.
- Has a value greater than 0.
- Throws a validation failure on parsing or non-positive value.

### `public ValidationBuilder validateQuantity()`
Validates the `quantity` field:
- Is present and not blank.
- Has a value greater than 0.
- Throws a validation failure on parsing or non-positive value.

### `public ValidationBuilder validateUserId()`
Validates the `userIdMust exist` field:
- Is present and not blank.
- Is in the `users` collection in MongoDB.

### `public ValidationBuilder validateOrderId()`
Validates the `orderId` field:
- Is present and not blank.
- Exists in the `activeOrders` collection in MongoDB.

### `public ValidationBuilder validateOrderType()`
Validates the `type` field:
- Is present and not blank.
- Is a value in the `OrderType` enum.

### `public ValidationBuilder validateTicker()`
Validates the `ticker` field:
- Is present and not blank.
- Is a value in the `Ticker` enum.

### `public ValidationBuilder validateUserBalanceAndPorfolio()`
Dynamically checks either:
- **If order type is `SELL`**: calls `userPortfolioIsSufficientForSell(JsonObject)` to ensure the user holds enough stock to cover the sell they are placing.
- **If order type is `BUY`**: calls `userBalanceIsSufficientForBuy(JsonObject)` to ensure the user has enough balance to cover their BUY.

*Note: Relies on utility methods imported statically from `DatabaseUtils`.*

### `public ValidationBuilder validateOrderToCancelBelongsToUser()`
Verifies:
- That the order exists in the `activeOrders` collection.
- That the order belongs to the user making the cancellation request.

Fails validation if:
- The order does not exist.
- Or the order's `userId` does not match the request's `userId`. 
### `public Validation build()`
Constructs and returns a final `Validation` instance that:
- Iterates through the chain of validations.
- Returns the first failing `ValidationResult`.
- Returns `ValidationResult.ok()` if all validations pass.

---


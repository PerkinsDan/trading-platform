# Validation

**Package**: `com.tradingplatform.orderprocessor.validations`  
**Filename**: `Validation.java`

## What does it do?

- `Validation` is a functional interface that represents a single validation rule for an incoming `JsonObject` request body.
- It is designed to validate specific fields or conditions and return a `ValidationResult`.

## When do we use it?

- Used when defining and applying individual validation logic in the `ValidationBuilder` class.
- Each lambda checks part of a request and returns either a success or failure result.

## Methods

- **`validate(JsonObject body)`**  
  Accepts a `JsonObject` and returns a `ValidationResult`.  
  This method is implemented using a lambda or method reference and encapsulates one validation rule.

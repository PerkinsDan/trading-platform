# ValidationResult

**Package**: `com.tradingplatform.orderprocessor.validations`  
**Filename**: `ValidationResult.java`

## What does it do?

- Utility class used to represent the result of a validation.
- Encapsulates whether a validation passed or failed, and if failed, includes an error message.

## When do we use it?

- Used by each `Validation` to indicate whether the input is valid.
- Helps to implement a fail-fast effect for `ValidationBuilder` if a failure occurs.
-A failed `Validation` will stop further validations from being done which could cause errors.

## Attributes

- **`isValid`**: A boolean indicating whether the validation passed.
- **`errorMessage`**: A descriptive string message explaining why validation failed (if applicable).

## Methods

- **`ok()`**:  
  Static factory method to create a successful `ValidationResult`.

- **`fail(String errorMessage)`**:  
  Static factory method to create a failure `ValidationResult` with an accompanying error message.

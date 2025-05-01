# OrderComparator

**Filename**: `OrderComparator.java`

## What does it do?

- `OrderComparator` implements the `Comparator` interface and overrides the `compare` method.
- Overriding the `compare` method allows us to sort orders by **price-time priority**, which is essential to ensure orders are matched properly.

## When do we use it?

- `OrderComparator` is used in the `TradeBook` class, which utilizes the `PriorityQueue` data structure.
- `PriorityQueue` orders elements using "natural ordering," which generally means smallest first.
- We override this behavior by defining our own `Comparator`, called `OrderComparator`, to sort orders in the specific way we want.

## Attributes

- **`OrderType`**: Specifies whether the order is a `BUY` or `SELL`.

## Methods

### `compare(Order order1, Order order2)`

- **Description**:
  - Compares two orders and returns either `1` or `-1` to determine which order comes first in the queue.
  - The comparison is based on **price-time priority**.

---

## Additional Notes

### Price-Time Priority (PTP)

- **Definition**: PTP is the algorithm used to determine which orders are given priority in terms of when they are processed.
- **Key Points**:
  - **Price**: Buy orders are sorted to have the highest prices first, and sell orders are sorted to have the lowest prices first.
  - **Time**: If two orders have the same price, the order submitted earlier is processed first.
  - **Preservation**: Priority is preserved until an order is modified.
- **Summary**: Better-priced orders execute first. At the same price, earlier orders get priority.

---

### Key Fixes:

1. **Improved Formatting:**

   - Added proper headers (`#`, `##`, `###`) for better structure and readability.
   - Used bullet points for lists and descriptions.
   - Added backticks (`) for code elements like class names, methods, and attributes.

2. **Removed `<br>` Tags:**

   - Replaced unnecessary `<br>` tags with proper Markdown spacing.

3. **Corrected Typos:**

   - Fixed "behaviour" → "behavior" (for consistency with American English).
   - Fixed "out TradeBooks" → "our TradeBooks."
   - Fixed "most importatnt thing" → "most important thing."

4. **Enhanced Clarity:**
   - Improved descriptions for attributes and methods to make them more concise and professional.

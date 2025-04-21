**What does it do?**

OrderComparator implements the Comparator interface, it overrides the
compare method. Overriding the compare method allows us to sort orders 
by price-time priority\*, which is essential to ensure orders are matched
properly.

<br>

**When do we use it?**

OrderComparator is used in the `TradeBook` class, which uses 
the PriorityQueue data structure. PriorityQueues order elements
using 'natural ordering' which generally means smallest first. We override 
this behaviour by defining our own Comparator, which we call OrderComparator,
and use it to sort Orders in out TradeBooks in the specific way we want.

<br>

**Attributes**

* OrderType : BUY or SELL.

<br>

**Methods**

compare (Order order1, Order order2)
* Compares any two orders and returns either 1 or -1 to determine which Order comes first in the queue.

<br>
<br>

\* Price-Time Priority (PTP) : PTP is the algorithm used to determine which orders are given priority in terms of when they are processed, as its not always about who submitted their order first. The most importatnt thing when sorting orders is price: buy orders are sorted to have highest prices first, and sell orders are sorted to have
lowest prices first (see OrderMatchingExplained.md). If two orders have the same price, then we compare the time they were submitted, and orders submitted earlier are processed first. Briefly,better-priced orders execute first, at the same price, earlier orders get priority and priority is preserved until an order is modified.



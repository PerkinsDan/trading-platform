**What is it?**

The MatchingEngine class is a utiltiy class filled with static methods. 
It contains the core order matching methods that are called each time an order
is placed. Its role is to look at a TradeBook and find as many matches between
orders as it can, create MatchingDetails DTOs for each match and return them.

<br>

**When do we use it?**

During trading hours, this class is called every single time an order is submitted. On each call, it evalutes the TradeBook, matches orders and generates DTOs for each match until there are no more possible matches. It is called exactly once per order, but each call may result in more than one match, as orders are matched in price-time
priority (see OrderComparator for further explanation) and an order being filled means a new order is now first in line to be matched.

<br>

**Attributes**

* buyOrders : PriorityQueue of buy orders.

* sellOrders : PriorityQueue of sell orders
* matchesFound : ArrayList of JSON Strings, each JSON is a serialised version of a MatchingDetails
object.

<br>

**Methods**

match(TradeBook tradeBook)
* Takes a `TradeBook` as input, evaluates the TradeBook for matches, process them and returns an ArrayListof `MatchingDetails` JSONs that are used to update the DB.

processMatches(): 
* Helper method called by the `match()` method: this method is responsible for updating orders in the TradeBook
* Adjusts quantities, 
* Creates and serialising the `MatchingDetails` objects, appends to the matchesFound list
* Removes completed orders from the `TradeBook`, so we dont erroneously process them again.

matchPossible(buyOrders, sellOrders): 

* Helper method used by the `match()` method.
* ooks at both sides of the TradeBook and checks is there is exactly one match possible, returns true while one match is possible and false when there arent any matches possible. 


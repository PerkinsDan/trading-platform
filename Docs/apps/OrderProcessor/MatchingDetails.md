**What is it?**

The MatchingDetails class is used to capture information about the details of a trade match, two are generated per match: one for the buy order and one for the sell order. The MatchingDetails object is used to update information about orders and users in the database.

<br>

**When do we use it?**

Anytime an order is submitted, it is added to the relevant TradeBook where it can be matched. When two orders match, we create a MatchingDetails object for each and pass these MatchingDetails objects that act as DTOs to the API, which uses them to update information in the DB.

<br>

**Attributes:**

* orderID : A unique identifier pulled from the order that was matched, to ensure we update the right order.
* price : the price at which the order was matched.
* quantityChange : The quantity (number of stocks) that was matched,as not all orders are filled completely on the first match.
* filled : A boolean identifer that is used to identify whether an order has been filled entirely.

<br>

**Methods:**

getOrderID()
* returns orderID atttribute

getPrice()
* returns price atttribute

getQuantityChange()
* return quantityChange atttribute

isFilled()
* returns True/False if order has been filled or not

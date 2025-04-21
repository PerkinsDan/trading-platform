**What is it?**

An Order represents an intention to buy or sell a specific amount, of a specifc stock at a specifc price. The Order class is the Data Model Object (DMO) used to represent the intent of an agent to engage in a trade. Each order object represents one side of a trade (ie there is an order object for both the buy and sell sides of a trade), and is treated as an individual
entity rather than as part of a 'trade'.

<br>

**When do we use it?**

An Order object is created when an Order is placed, it is persisted in the database, while it is processed, upon being filled, it is marked as 'filled' and warehoused in case its needed for reference or regulatory reporting.

<br>

**Attributes**

* orderId : A UUID, used to identify the order through its lifetime, generated on instantiation.
* type : BUY or SELL, determined by user.
* price : the price that the order is to be executed at, determined by user
* timestamp : the time at which the order was submitted.
* Ticker : a symbol to represent which stock the user is interested in buying or selling.
* quantity : the amount of a stock that the user wants to buy otr sell.
* cancelled : false by default, only changed in case a cancellation request is submitted.
* filled : false by defualt, onky changed when an order has been completely filled.
* UserId : used to assign each order to a specific user,

<br>

**Methods**

* Getters for the various attributes.
* reduceQuantity(int amount) : decreases the quantity attribute of the order.
* toString() : useful for logging, displays attributes in dictionary format.
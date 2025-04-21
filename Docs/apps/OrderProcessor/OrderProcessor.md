**What does it do?**

* OrderProcessor is the top level class of the OrderProcessor micro-
service, it stores and processes active orders and interfaces
with the API. 
* It is a Singleton, this ensures thread-safety and 
universal access control. 
* OrderProcessor aggregates TradeBooks which
in turn aggregate Orders, and stores all of them in a map to provide
API with a single point of contact with the core funcionality of the 
OrderProcessor service. 
* It manages adding orders to relevant books, 
matching orders, and order cancellation.

<br>

**When do we use it?**

Since it is the top level class of the OrderProcessor service, and
serves as the interface for the API it is used any time the OrderProcessor service is called.

<br>

**Attributes**

* orderProcessor : The single instance of the class.
* tradeBookMap : HashMap that stores a TradeBook for each ticker

<br>

**Methods**

OrderProcessor(): 
* Private constructor

getInstance(): 
* Static method to retrieve Singleton

processOrder(Order order): 
* This is a composite method, it adds an Order to the relevant TradeBook, evaluated for matches, ands returns the list of MatchingDetails objects expressed as JSON to the API to be used to update the DB.

cancelOrder(Order order): 
* Method to cancel orders, finds the Order to be cancelled in theappropriate TradeBook and removes it, returns True if order was removed sucessfully, and False if it couldnt find the order.
* Returnning false would mean that the order had already been filled and taken off the books by the time the cancellation came in, at which point nothing further can be done.
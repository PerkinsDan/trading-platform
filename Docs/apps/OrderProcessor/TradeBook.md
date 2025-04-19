<u>What does it do ?</u>

* A TradeBook holds all if the active BUY and SELL orders for a
particular stock. BUY AND SELL orders are held in separate PriorityQueues
* The PriorityQueues sort orders according to price-time priority automatically, meaning we dont ever have to worry abut sorting the orders ourselves. We can just poll Orders from the front of hte queue and match them.

<br>
<u>When do we use it?</u>

* TradeBook is used by in the TradeBookMap attribute of the OrderProcessor class.
* Its mainly useful for keeping our Orders sorted correctly. We create the TradeBookMap
and by extension the TradeBook objects on startup of the app and keep them in-memory 
indefinitely.

<br>
<u>Attributes</u>

* buyOrders : PriorityQueue of buy orders, sorted with hishest price first
* sellOrders : PriorityQueue of sell orders, sorted for lowest price first

<br>
<u>Methods</u>

* addToBook(Order order) : Adds order to the relevant side (BUY/SELL) of the TradeBook
* getBuyOrders() : returns queue of buy orders
* getSellOrders() : returns queue of sell orders
    

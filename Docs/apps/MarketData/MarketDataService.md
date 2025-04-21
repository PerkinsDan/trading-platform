**What does it do?**

* MarketDataService is the top level class of the MarketData microservice and
acts as the interface to the MarketData api. 
* On instantiation, it checks if we are starting up the app, if we are it makes a days worth of data, otherwise it checks every minute if it is currently market open. 
* The MarketDataService runs with a Daemon thread as the main thread is reserved for hadnling API calls.
* Created as a Singleton for thread-safety and global access contol, so all market data comes from the same source.

<br>

**When do we use it?**

Anytime we need maket data: the front-end LiquidityEngine. 

<br>

**Attributes**

* simulateData  : Access point to simulateData class and generateData() method.
* marketDataServiceHolder : Singleton object for the class.

<br>

**Methods** 

MarketDataService()
 * creates service object and starts the Daemon thread.

getInstance()
* MarketDataService : used to retreive the single instance of 
MarketDataService

getTimeSeries(Ticker)
* returns the TimeSeries associated with a Ticker upto the current time

getLatestSnapshot(Ticker)
* Returns the Snapshot associated with the current time.
**What does it do?**

* SimulateData aggregates `TimeSeries`, which in turn aggregate `Snapshots`. 
* SimulateData contains a hashmap of `TimeSeries` objects, each `TimeSeries` is mapped to a `Ticker` and is used to retieve 'market data' every 5 minutes.
* We use randomly generated data, this is because we couldnt find a free market data API that fit our requirements.
* The main functionalty here is in the `generateData()` method, it creates a days worth timeseries data in advance, so all the data is ready in advance of market open.

**When do we use it?**

* Used heavily by the front end to generate time series graphs of prices for each ticker.
Market price is also used in the front end as reference when placing an order.
* Used in the LiquidityEngine service (coming soon), as a starting price to generate orders around.

**Attributes**

* marketOpenTime : Time-zone agnostic open time set at 9:30am
* marketCloseTime : Time-zone agnostic close time set at 4:00pm
* timeSeriesMap : the hashmap containing the TimeSeries for each ticker

**Methods**

SimulateData():

* Constructor - creates the timeSeriesMap, empty on instantiation

getTimeSeries(Ticker):

*  Returns the TimeSeries that corresponds to the Ticker passed in

generateData(): 
* For each Ticker in the timeSeriesMap, it starts at marketOpenTime, generates a price and creates a Snapshot, and repeats this process of randomnly generting a price and creating snapshot for every 5 minures until market close time.
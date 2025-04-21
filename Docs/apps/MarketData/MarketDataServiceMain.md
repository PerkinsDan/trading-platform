**What does it do?**

* This class is the entry point for the MarketData API, it starts 
an HTTP server to serve market data. 
* It uses the [Vert.x toolkit](https://vertx.io/). 
* It defines the base URL ("/") and uses the `marketDataRouter` class as a subrouter to specify endpoints and their functionality.

<br>

**Where do we use it?**

This class sets up the HTTP server for the market data API, we use it any time a MarketData API endpoint is hit to parse and handle requests.

<br>

**Attributes**

* marketDataService : Serves as the interface for all our marketdata, used to retreive data used to fulfill http requests

<br>

**Methods**

main(String[] args);
* Main method which starts the Vertx http server by calling `startServer()`.

startServer(): 
* Creates http server.
* Defines base url path, creates a router and specifies to use the marketDataRouter to define endpoints.
* Opens a connection at port 12000.
**What does it do**

This MarketDataRouter class is part of a Java application that uses the Vert.x framework to handle HTTP requests and responses. It acts as a router for market data-related endpoints, defining how the application should respond to specific HTTP routes.

**When do we use it?**

This class defines endpoints and how to respond to requests made at those endpoints



**Attributes**

* JSON_ERROR_MESSAGE: String used as an error message when JSON covnersion fails.
* router: An instance of Vert.x's Router class, which is responsible for defining and handling HTTP routes.
* marketDataService: A service class that provides market data suchas snapshots and time series.


**Methods**

MarketDataRouter(Vertx vertx, MarketDataService marketDataService)
setupRoutes():
 
&nbsp;&nbsp;Defines two routes: Each route extracts the ticker parameter from the URL, calls the the appropriate method in &nbsp;&nbsp;marketDataService, and sends the response as JSON.

* /latest-snapshot/:ticker: Fetches the latest market data snapshot for a given ticker symbol.
* /time-series/:ticker: Fetches a time series of market data for a given ticker symbol.

    
sendJsonResponse(Rounting ctx, Object data):
* Converts the given data object to a JSON string using Jackson's ObjectMapper.
* Sends the JSON response back to the client.
* If JSON conversion fails, it calls sendErrorResponse().
    
sendErrorRespo(RoutingContext ctx):
* Sends a 500 Internal Server Error response with the JSON_ERROR_MESSAGE.getRouter()
* Exposes the router instance so it can be used elsewhere in the application.

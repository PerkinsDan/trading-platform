# Market Data Router

**Filename**: `MarketDataRouter.java`

## What does it do

This `MarketDataRouter` class is part of a Java application that uses the Vert.x framework to handle HTTP requests and responses. It acts as a router for market data-related endpoints, defining how the application should respond to specific HTTP routes.

## When do we use it?

This class defines endpoints and how to respond to requests made at those endpoints.

## Attributes

- **`JSON_ERROR_MESSAGE`**: String used as an error message when JSON conversion fails.
- **`router`**: An instance of Vert.x's `Router` class, which is responsible for defining and handling HTTP routes.
- **`marketDataService`**: A service class that provides market data such as snapshots and time series.

## Methods

### `MarketDataRouter(Vertx vertx, MarketDataService marketDataService)`

- **Constructor**: Initializes `router` and `marketDataService`.

### `setupRoutes()`

Defines two routes. Each route extracts the `ticker` parameter from the URL, calls the appropriate method in `marketDataService`, and sends the response as JSON.

- **`/latest-snapshot?ticker=`**: Fetches the latest market data snapshot for a given ticker symbol.
- **`/time-series?ticker=`**: Fetches a time series of market data for a given ticker symbol.

### `parseTicker()`

Validates the ticker provided:

- A string is provided
- It is present in `Ticker`

It then returns the equivalent `Ticker`

### `sendResponse(RoutingContext ctx, Object data)`

- Converts the given `data` object to a JSON string using Jackson's `ObjectMapper`.
- Sends the JSON response back to the client.
- If JSON conversion fails, it sends a `500 Internal Server Error` response with the `JSON_ERROR_MESSAGE`.

### `getRouter()`

- Exposes the `router` instance so it can be used elsewhere in the application.

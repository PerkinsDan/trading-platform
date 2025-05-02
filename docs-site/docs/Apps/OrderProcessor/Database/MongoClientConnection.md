# Database Utils

**Filename**: `DatabaseUtils.java`

## What does it do

`DatabaseUtils` contains methods useful for manipulating our MongoDB database to reflect users creating accounts, orders being made, trades being executed and any other interaction that requires the database to be updated.

## When do we use it?

The methods contained in this class are used by the Vert.x API's endpoints.

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

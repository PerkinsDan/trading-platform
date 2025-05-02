# Market Data Service Main

**Filename**: `MarketDataServiceMain.java`

## What does it do?

- This class is the entry point for the MarketData API. It starts an HTTP server to serve market data.
- It uses the [Vert.x toolkit](https://vertx.io/).
- It defines the base URL (`"/"`) and uses the `MarketDataRouter` class as a sub-router to specify endpoints and their functionality.

## Where do we use it?

- This class sets up the HTTP server for the MarketData API.
- It is used whenever a MarketData API endpoint is hit to parse and handle requests.

## Attributes

- **`marketDataService`**: Serves as the interface for all market data, used to retrieve data to fulfill HTTP requests.

## Methods

### `main(String[] args)`

- **Description**: The main method that starts the Vert.x HTTP server by calling `startServer()`.

### `startServer()`

- **Description**:
  - Creates the HTTP server.
  - Defines the base URL path, creates a router, and specifies the use of `MarketDataRouter` to define endpoints.
  - Opens a connection at port `12000`.

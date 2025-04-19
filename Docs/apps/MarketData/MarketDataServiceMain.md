What does it do?
    This class is the entry point for the MarketData API, it starts 
    an HTTP server to serve market data. It uses the [Vert.x toolkit](https://vertx.io/). It defines the base URL ("/") and uses the
    marketDataRouter class as a subrouter to specify endpoints and their functionality.

Where do we use it?
    This class sets up the HTTP server for the market data API, we use it any time a MarketData API endpoint is hit to parse and handle requests.

Attributes
    marketDataService : Serves as the interface for all our market
                        data, used to retreive data used to fulfill 
                        http requests
Methods
    main(args) : Main method which starts the Vertx http server
    startServer() : Creates http server, defines base url path, creates
                    a router and specifies to use the marketDataRouter 
                    to define endpoints, and opens a connection at port 12000.
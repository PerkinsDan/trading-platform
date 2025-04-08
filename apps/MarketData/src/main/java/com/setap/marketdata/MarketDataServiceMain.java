package com.setap.marketdata;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

public class MarketDataServiceMain {

  private static MarketDataService marketDataService;

  public static void main(String[] args) {
    marketDataService = MarketDataService.getInstance();
    startServer();
  }

  private static void startServer() {
    Vertx vertx = Vertx.vertx();
    HttpServer server = vertx.createHttpServer();

    Router router = Router.router(vertx);

    Router marketDataRouter = new MarketDataRouter(
      vertx,
      marketDataService
    ).getRouter();
    router.route().subRouter(marketDataRouter);

    server.requestHandler(router).listen(8080);
  }
}

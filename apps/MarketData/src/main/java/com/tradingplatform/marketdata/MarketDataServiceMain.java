package com.tradingplatform.marketdata;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;

public class MarketDataServiceMain {

  private static MarketDataService marketDataService;
  private static Router router;

  public static void main(String[] args) {
    marketDataService = MarketDataService.getInstance();
    startServer();
  }

  private static void startServer() {
    Vertx vertx = Vertx.vertx();
    HttpServer server = vertx.createHttpServer();

    router = Router.router(vertx);
    enableCors();

    Router marketDataRouter = new MarketDataRouter(
      vertx,
      marketDataService
    ).getRouter();

    router.route().subRouter(marketDataRouter);

    server.requestHandler(router).listen(12000);
  }

  private static void enableCors() {
    router
      .route()
      .handler(
        CorsHandler.create()
          .addOrigin("*")
          .allowedHeader("Content-Type")
          .allowedHeader("Authorization")
      );
  }
}

package com.setap.marketdata;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;

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
    router
      .route()
      .handler(
        CorsHandler.create()
          .addOrigin("*")
          .allowedHeader("Content-Type")
          .allowedHeader("Authorization")
      );

    Router marketDataRouter = new MarketDataRouter(
      vertx,
      marketDataService
    ).getRouter();

    router
      .route("/")
      .handler(ctx -> {
        ctx.response().end("Welcome to the Market Data Service");
      });

    router.route().subRouter(marketDataRouter);

    server.requestHandler(router).listen(12000);
  }
}

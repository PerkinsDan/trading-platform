package com.tradingplatform.orderprocessor;

import static com.tradingplatform.orderprocessor.database.MongoClientConnection.createConnection;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;

public class OrderProcessorServiceMain {

  private static OrderProcessorService orderProcessorService;

  public static void main(String[] args) {
    orderProcessorService = OrderProcessorService.getInstance();
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

    Router orderProcessorRouter = new OrderProcessorRouter(
      vertx,
      orderProcessorService
    ).getRouter();

    router.route().subRouter(orderProcessorRouter);

    server.requestHandler(router).listen(12000);

    if (!createConnection()) System.exit(-1);
  }
}

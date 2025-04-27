package com.tradingplatform.orderprocessor;

import static com.tradingplatform.orderprocessor.database.MongoClientConnection.createConnection;

import com.tradingplatform.orderprocessor.routers.MasterRouter;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;

public class OrderProcessorServiceMain {

  private static OrderProcessorService orderProcessorService;

  public static void main(String[] args) {
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

    Router masterRouter = new MasterRouter(vertx).getRouter();
    router.route().subRouter(masterRouter);

    server.requestHandler(router).listen(8080);

    if (!createConnection()) System.exit(-1);

    System.out.println("Starting server...");
  }
}

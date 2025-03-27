package com.setap.tradingplatformapi;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
    HttpServer server = vertx.createHttpServer();

    Router router = new MainRouter(vertx).router;

    server
      .requestHandler(router)
      .listen(8080)
      .<Void>mapEmpty()
      .onComplete(startPromise);
  }

  public static void main(String[] args) {
    System.out.println("Starting server...");
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }
}

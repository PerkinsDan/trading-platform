package org.example;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

public class MainRouter {

  Router router;

  MainRouter(Vertx vertx) {
    router = Router.router(vertx);

    initialise();
  }

  void initialise() {
    router
      .route("/")
      .handler(ctx -> {
        HttpServerResponse response = ctx.response();
        response.putHeader("content-type", "text/plain");
        response.end("Hello World from Vert.x-Web!");
      });

      router.get("/user-active-positions").handler(ctx -> {
          // logic to retrieve user's active trade positions
          ctx.response().end("Retrieving user's active positions...");
      });

      router.get("/user-trade-history").handler(ctx -> {
          // logic to retrieve user's trade history
          ctx.response().end("Retrieving user's trade history...");
      });

      router.get("/orders").handler(ctx -> {
          // logic to retrieve all active orders
          ctx.response().end("Retrieving all orders...");
      });

      router.post("/create-user").handler(ctx -> {
          // logic to create a new user
          ctx.response().end("Creating a new user...");
      });
  }
}


//user-active-positions
//user-trade-history
//orders
//create-order
//create-user
//update-user-balance
//get-user-account (to get balance)
//market-prices

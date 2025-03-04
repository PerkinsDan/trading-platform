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
  }
}

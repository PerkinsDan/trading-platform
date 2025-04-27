package com.tradingplatform.orderprocessor.routers;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class MasterRouter {

  private final Router router;

  public MasterRouter(Vertx vertx) {
    router = Router.router(vertx);

    Orders orders = new Orders(vertx);
    router.route("/orders/*").subRouter(orders.getRouter());

    UsersRouter usersRouter = new UsersRouter(vertx);
    router.route("/users/*").subRouter(usersRouter.getRouter());
  }

  public Router getRouter() {
    return router;
  }
}

package com.tradingplatform.orderprocessor.routers;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class MasterRouter {

  private final Router router;

  public MasterRouter(Vertx vertx) {
    router = Router.router(vertx);

    OrdersRouter ordersRouter = new OrdersRouter(vertx);
    router.route("/orders/*").subRouter(ordersRouter.getRouter());

    UsersRouter usersRouter = new UsersRouter(vertx);
    router.route("/users/*").subRouter(usersRouter.getRouter());
  }

  public Router getRouter() {
    return router;
  }
}

package com.setap.marketdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.setap.marketdata.constants.Tickers;
import com.setap.marketdata.simulatedata.Snapshot;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import java.util.ArrayList;

public class MarketDataRouter {

  private static final String JSON_ERROR_MESSAGE = "Error converting to JSON";
  private final Router router;
  private final MarketDataService marketDataService;

  public MarketDataRouter(Vertx vertx, MarketDataService marketDataService) {
    this.router = Router.router(vertx);
    this.marketDataService = marketDataService;

    setupRoutes();
  }

  private void setupRoutes() {
    router
      .route("/latest-snapshot/:ticker")
      .handler(ctx -> {
        String ticker = ctx.request().getParam("ticker");

        Snapshot snapshot = marketDataService.getLatestSnapshot(
          Tickers.valueOf(ticker)
        );
        sendJsonResponse(ctx, snapshot);
      });

    router
      .route("/time-series/:ticker")
      .handler(ctx -> {
        String ticker = ctx.request().getParam("ticker");

        ArrayList<Snapshot> timeSeries = marketDataService.getTimeSeries(
          Tickers.valueOf(ticker)
        );
        sendJsonResponse(ctx, timeSeries);
      });
  }

  private void sendJsonResponse(RoutingContext ctx, Object data) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();

      String response = objectMapper.writeValueAsString(data);
      ctx.response().end(response);
    } catch (Exception e) {
      sendErrorResponse(ctx);
    }
  }

  private void sendErrorResponse(RoutingContext ctx) {
    ctx.response().setStatusCode(500).end(MarketDataRouter.JSON_ERROR_MESSAGE);
  }

  public Router getRouter() {
    return router;
  }
}

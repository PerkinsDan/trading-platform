package com.tradingplatform.marketdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tradingplatform.marketdata.constants.Ticker;
import com.tradingplatform.marketdata.simulatedata.Snapshot;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import java.util.ArrayList;

public class MarketDataRouter {

  private static final String JSON_ERROR_MESSAGE = "Error converting to JSON";
  private final Router router;
  private final MarketDataService marketDataService;

  public MarketDataRouter(Vertx vertx, MarketDataService marketDataService) {
    router = Router.router(vertx);
    this.marketDataService = marketDataService;

    setupRoutes();
  }

  private void setupRoutes() {
    router
      .route("/latest-snapshot")
      .handler(ctx -> {
        Ticker ticker = parseTicker(ctx);
        if (ticker == null) return;

        System.out.println("Getting latest snapshot: " + ticker);

        Snapshot snapshot = marketDataService.getLatestSnapshot(ticker);

        sendResponse(ctx, snapshot);
      });

    router
      .route("/time-series")
      .handler(ctx -> {
        Ticker ticker = parseTicker(ctx);
        if (ticker == null) return;

        System.out.println("Getting latest time series: " + ticker);

        ArrayList<Snapshot> timeSeries = marketDataService.getTimeSeries(
          ticker
        );

        sendResponse(ctx, timeSeries);
      });
  }

  private Ticker parseTicker(RoutingContext ctx) {
    String tickerInput = ctx.request().getParam("ticker");

    if (tickerInput == null || tickerInput.isEmpty()) {
      ctx
        .response()
        .setStatusCode(400)
        .end("Ticker parameter is missing or invalid");
      return null;
    }

    try {
      return Ticker.valueOf(tickerInput.toUpperCase());
    } catch (IllegalArgumentException e) {
      ctx
        .response()
        .setStatusCode(400)
        .end("Ticker is invalid: " + tickerInput);
      return null;
    }
  }

  private void sendResponse(RoutingContext ctx, Object data) {
    try {
      ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule());

      String response = objectMapper.writeValueAsString(data);
      ctx.response().end(response);
    } catch (Exception e) {
      System.out.println(JSON_ERROR_MESSAGE + e);
      ctx
        .response()
        .setStatusCode(500)
        .end(MarketDataRouter.JSON_ERROR_MESSAGE);
    }
  }

  public Router getRouter() {
    return router;
  }
}

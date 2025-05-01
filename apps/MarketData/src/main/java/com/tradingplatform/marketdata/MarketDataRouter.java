package com.tradingplatform.marketdata;

import com.fasterxml.jackson.core.JsonProcessingException;
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
        String tickerInput = ctx.request().getParam("ticker");

        if (tickerInput == null || tickerInput.isEmpty()) {
          ctx
            .response()
            .setStatusCode(400)
            .end("Ticker parameter is missing or invalid");
          return;
        }

        Ticker ticker;

        try {
          ticker = Ticker.valueOf(tickerInput.toUpperCase());
        } catch (IllegalArgumentException e) {
          ctx
            .response()
            .setStatusCode(400)
            .end("Ticker is invalid: " + tickerInput);
          return;
        }

        System.out.println("Getting latest snapshot: " + ticker);

        Snapshot snapshot = marketDataService.getLatestSnapshot(ticker);

        ObjectMapper objectMapper = new ObjectMapper()
          .registerModule(new JavaTimeModule());

        try {
          String response = objectMapper.writeValueAsString(snapshot);
          ctx.response().end(response);
        } catch (JsonProcessingException e) {
          System.out.println("Error processing object to json: " + e);
          ctx.response().setStatusCode(500).end("Server error");
        }
      });

    router
      .route("/time-series/:ticker")
      .handler(ctx -> {
        String ticker = ctx.request().getParam("ticker");

        ArrayList<Snapshot> timeSeries = marketDataService.getTimeSeries(
          Ticker.valueOf(ticker)
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

package com.tradingplatform.marketdata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tradingplatform.marketdata.constants.Tickers;
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
      .route("/latest-snapshot/:ticker")
      .handler(ctx -> {
        String ticker = ctx.request().getParam("ticker");

        Snapshot snapshot = marketDataService.getLatestSnapshot(
          Tickers.valueOf(ticker)
        );

        ObjectMapper objectMapper = new ObjectMapper()
          .registerModule(new JavaTimeModule());

        String response = null;
        try {
          response = objectMapper.writeValueAsString(snapshot);
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
        ctx.response().end(response);
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

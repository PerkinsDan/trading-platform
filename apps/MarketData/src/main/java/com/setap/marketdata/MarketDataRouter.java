package com.setap.marketdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import java.util.ArrayList;

public class MarketDataRouter {

  private final Router router;
  private final MarketDataService marketDataService;

  public MarketDataRouter(Vertx vertx, MarketDataService marketDataService) {
    this.router = Router.router(vertx);
    this.marketDataService = marketDataService;

    setupRoutes();
  }

  private String convertToJson(Object object) {
    try {
      ObjectMapper mapper = new ObjectMapper()
        .registerModule(new JavaTimeModule());

      return mapper.writeValueAsString(object);
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      return null;
    }
  }

  private void setupRoutes() {
    router
      .route("/latest-snapshot/:ticker")
      .handler(ctx -> {
        String ticker = ctx.request().getParam("ticker");

        Snapshot snapshot = marketDataService.getLatestSnapshot(
          Tickers.valueOf(ticker)
        );

        String res = convertToJson(snapshot);

        if (res == null) {
          ctx.response().setStatusCode(500).end("Error converting to JSON");
          return;
        }

        ctx.response().end(res);
      });

    router
      .route("/time-series/:ticker")
      .handler(ctx -> {
        String ticker = ctx.request().getParam("ticker");

        ArrayList<Snapshot> timeSeries = marketDataService.getTimeSeries(
          Tickers.valueOf(ticker)
        );

        String res = convertToJson(timeSeries);

        if (res == null) {
          ctx.response().setStatusCode(500).end("Error converting to JSON");
          return;
        }

        ctx.response().end(res);
      });
  }

  public Router getRouter() {
    return router;
  }
}

import React from "react";
import ComponentCreator from "@docusaurus/ComponentCreator";

export default [
  {
    path: "/markdown-page",
    component: ComponentCreator("/markdown-page", "3d7"),
    exact: true,
  },
  {
    path: "/docs",
    component: ComponentCreator("/docs", "386"),
    routes: [
      {
        path: "/docs",
        component: ComponentCreator("/docs", "cac"),
        routes: [
          {
            path: "/docs",
            component: ComponentCreator("/docs", "e64"),
            routes: [
              {
                path: "/docs/Apps/MarketData/Constants/Ticker",
                component: ComponentCreator(
                  "/docs/Apps/MarketData/Constants/Ticker",
                  "894",
                ),
                exact: true,
                sidebar: "tutorialSidebar",
              },
              {
                path: "/docs/Apps/MarketData/MarketDataRouter",
                component: ComponentCreator(
                  "/docs/Apps/MarketData/MarketDataRouter",
                  "4c5",
                ),
                exact: true,
                sidebar: "tutorialSidebar",
              },
              {
                path: "/docs/Apps/MarketData/MarketDataService",
                component: ComponentCreator(
                  "/docs/Apps/MarketData/MarketDataService",
                  "79d",
                ),
                exact: true,
                sidebar: "tutorialSidebar",
              },
              {
                path: "/docs/Apps/MarketData/MarketDataServiceMain",
                component: ComponentCreator(
                  "/docs/Apps/MarketData/MarketDataServiceMain",
                  "2bb",
                ),
                exact: true,
                sidebar: "tutorialSidebar",
              },
              {
                path: "/docs/Apps/MarketData/Simulate Data/SimulateData",
                component: ComponentCreator(
                  "/docs/Apps/MarketData/Simulate Data/SimulateData",
                  "a0c",
                ),
                exact: true,
                sidebar: "tutorialSidebar",
              },
              {
                path: "/docs/Apps/MarketData/Simulate Data/Snapshot",
                component: ComponentCreator(
                  "/docs/Apps/MarketData/Simulate Data/Snapshot",
                  "947",
                ),
                exact: true,
                sidebar: "tutorialSidebar",
              },
              {
                path: "/docs/Apps/MarketData/Simulate Data/TimeSeries",
                component: ComponentCreator(
                  "/docs/Apps/MarketData/Simulate Data/TimeSeries",
                  "eb6",
                ),
                exact: true,
                sidebar: "tutorialSidebar",
              },
              {
                path: "/docs/Apps/OrderProcessor/",
                component: ComponentCreator(
                  "/docs/Apps/OrderProcessor/",
                  "fa2",
                ),
                exact: true,
                sidebar: "tutorialSidebar",
              },
              {
                path: "/docs/Apps/OrderProcessor/MatchingDetails",
                component: ComponentCreator(
                  "/docs/Apps/OrderProcessor/MatchingDetails",
                  "794",
                ),
                exact: true,
                sidebar: "tutorialSidebar",
              },
              {
                path: "/docs/Apps/OrderProcessor/MatchingEngine",
                component: ComponentCreator(
                  "/docs/Apps/OrderProcessor/MatchingEngine",
                  "8dc",
                ),
                exact: true,
                sidebar: "tutorialSidebar",
              },
              {
                path: "/docs/Apps/OrderProcessor/Order",
                component: ComponentCreator(
                  "/docs/Apps/OrderProcessor/Order",
                  "143",
                ),
                exact: true,
                sidebar: "tutorialSidebar",
              },
              {
                path: "/docs/Apps/OrderProcessor/OrderComparator",
                component: ComponentCreator(
                  "/docs/Apps/OrderProcessor/OrderComparator",
                  "e7b",
                ),
                exact: true,
                sidebar: "tutorialSidebar",
              },
              {
                path: "/docs/Apps/OrderProcessor/OrderType",
                component: ComponentCreator(
                  "/docs/Apps/OrderProcessor/OrderType",
                  "a46",
                ),
                exact: true,
                sidebar: "tutorialSidebar",
              },
              {
                path: "/docs/Apps/OrderProcessor/Ticker",
                component: ComponentCreator(
                  "/docs/Apps/OrderProcessor/Ticker",
                  "c15",
                ),
                exact: true,
                sidebar: "tutorialSidebar",
              },
              {
                path: "/docs/Apps/OrderProcessor/TradeBook",
                component: ComponentCreator(
                  "/docs/Apps/OrderProcessor/TradeBook",
                  "c32",
                ),
                exact: true,
                sidebar: "tutorialSidebar",
              },
              {
                path: "/docs/Diagrams/architecture",
                component: ComponentCreator(
                  "/docs/Diagrams/architecture",
                  "1d5",
                ),
                exact: true,
                sidebar: "tutorialSidebar",
              },
              {
                path: "/docs/Diagrams/businessFlows",
                component: ComponentCreator(
                  "/docs/Diagrams/businessFlows",
                  "635",
                ),
                exact: true,
                sidebar: "tutorialSidebar",
              },
              {
                path: "/docs/Diagrams/marketData",
                component: ComponentCreator("/docs/Diagrams/marketData", "7c6"),
                exact: true,
                sidebar: "tutorialSidebar",
              },
              {
                path: "/docs/Diagrams/orderProcessor",
                component: ComponentCreator(
                  "/docs/Diagrams/orderProcessor",
                  "ebe",
                ),
                exact: true,
                sidebar: "tutorialSidebar",
              },
              {
                path: "/docs/intro",
                component: ComponentCreator("/docs/intro", "61d"),
                exact: true,
                sidebar: "tutorialSidebar",
              },
              {
                path: "/docs/OrderMatchingExplained",
                component: ComponentCreator(
                  "/docs/OrderMatchingExplained",
                  "0dd",
                ),
                exact: true,
                sidebar: "tutorialSidebar",
              },
            ],
          },
        ],
      },
    ],
  },
  {
    path: "/",
    component: ComponentCreator("/", "e5f"),
    exact: true,
  },
  {
    path: "*",
    component: ComponentCreator("*"),
  },
];

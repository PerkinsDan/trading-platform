//mermaid script to create docs for the trading platform project
// go to https://mermaid.live and copy-paste the below mermaid script to see UML diagram for OrderProcesor
---
title: Package - Order Processor
---
%%Define classes
classDiagram
    class OrderProcessor{
         - orderProcessor OrderProcessor
         - tradeBookMap Map~Ticker,TradeBook~ 
         - OrderProcessor()
         + getInstance() OrderProcessor
         + processOrder(Order) ArrayList~String~         
         cd + getTradeBook(Ticker) TradeBook
         + cancelOrder(Order) boolean
         + resetInstance() void
    }
    class MatchingEngine{
        - buyOrders PriorityQueue~Order~
        - sellOrders PriorityQueue~Order~
        - matchesFound ArrayList~String~
        + match(TradeBook) ArrayList~String~
        - processMatches(Order, Order) void
        - matchesPossible(PriorityQueue~Order~, PriorityQueue~Order~) boolean
    }
    class TradeBook{
        - buyOrders PriorityQueue~Order~
        - sellOrders PriorityQueue~Order~
        + TradeBook()
        + addToBook(Order) void
        + getBuyOrders() PriorityQueue~Order~
        + getSellOrders() PriorityQueue~Order~
    }
    class MatchingDetails{
         - orderID UUID
         - price doublegit 
         - quantityChange int 
         - filled boolean
         + MatchingDetails()
         + getOrderID() UUID
         + getPrice() double
         + getQuantityChange() int
         + isFilled() boolean
    }
    class Order{
        - orderId UUID
        - type OrderType
        - price double
        - timestamp long
        - ticker Ticker
        - quantity int
        - cancelled boolean
        - filled boolean
        - userId String
        + getId() UUID
        + getType() OrderType
        + getPrice() double
        + getQuantity() int
        + getTicker() Ticker
        + reduceQuantity() void
        + toString() String
        + toDocument() Document
    }
    class OrderComparator{
        - type OrderType
        + compare(Order, Order) int
    }
    class OrderType{
        <<enumeration>>
        + BUY
        + SELL
    }
    class Ticker{
        note "Replace these with actual tickers at some point"
        <<enumeration>>
        + A
        + B
        + C 
        + D 
        + E
    }

%% Class relations
OrderProcessor ..> MatchingEngine : Depends On
OrderProcessor--* TradeBook : Composed Of
MatchingEngine ..> MatchingDetails : Depends On
Order --* Ticker : Composed Of
Order --* OrderType : Composed Of
TradeBook --o Order : Aggregates
TradeBook ..> OrderComparator : Depends On
Order



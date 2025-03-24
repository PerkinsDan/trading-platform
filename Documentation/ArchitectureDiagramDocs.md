<<<<<<< HEAD
```mermaid
graph RL;
    subgraph FrontEnd
        A[Web Interface]
    end

    subgraph OrderProcessor
        B[OrderProcessorAPI]
        C[MatchingEngine]
        B --> C
        C --> B
    end

    subgraph Storage
        E[MongoDB]
    end

    subgraph MarketData
        F[MarketDataAPI]
        G[MarketDataService]
        F --> G
        G --> F
    end

    subgraph LiquidityEngine
        K[LiquidityEngineAPI]
        L[LquidityService]
        K --> L
        L --> K
    end
    
    

    FrontEnd --> |Places Order| OrderProcessor
    OrderProcessor --> |Writes To| Storage
    OrderProcessor --> |Updates| Storage
    Storage --> |Feeds| WebHook
    WebHook --> |Updates| FrontEnd

    MarketData -->|Pulls From| LiquidityEngine
    MarketData --> |Feeds| FrontEnd

    LiquidityEngine --> |Streams To| OrderProcessor

    classDef cloud fill:#00FFFF,stroke:#00000,stroke-width:2px,color:#000000;
    class A,B,F,K,WebHook cloud;
```
=======
graph TD;
    subgraph Front End
        A[Web Interface]
    end

    subgraph API
        B --> C[Matching Engine]
        C --> B[Order Processor]
    end

    subgraph Storage
        D[MongoDB]
    end

    A --> API
    API--> |Updates| Storage

    E[Webhook] --> |listens to| Storage
    A <--> |Updates From| E[Webhook]
    A --> |Writes To| Storage 
    

    classDef cloud fill:#00FFFF,stroke:#00000,stroke-width:2px,color:#000000;
    classDef listens fill:#90EE90,stroke:#333,stroke-width:2px, color: #000000;
    class A,B,D cloud;
    class E listens;
>>>>>>> 19c4542 (Add mermaid script for architecture diagram)

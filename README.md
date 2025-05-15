# trading-platform

A trading platform for our group assignment (Software Engineering Tools Techniques and Practices)

## To run the app

`docker compose up`

The UI is hosted on `http://localhost:5173/`

## Development

### Development Guidlines

- Each separate microservice should be created in apps/ with its own pom.xml

### Husky

On Mac?
When trying to commit you may run into:

```zsh
hint: The '.husky/pre-commit' hook was ignored because it's not set as executable.
hint: You can disable this warning with `git config advice.ignoredHook false`.
```

In this case run:

```zsh
$ chmod ug+x .husky/*
(This makes the pre-commit file executable and you can rerun the git commit)
```

### Environment Vars

Inside `apps/client/.env :

```
VITE_APP_FIREBASE_API_KEY = ######
VITE_APP_ORDER_PROCESSOR_BASE_URL = http://localhost:8080
VITE_APP_MARKET_DATA_BASE_URL = http://localhost:12000
```

Inside `apps/OrderProcessor/.env :

```
DB_URI = #####
```

## TO TEST

To test you can try complete these trades. We have put in sell orders to ensure that your trades are fulfilled

Anything other than the trades below will go into pending unless another account goes to do an order at the same price in the opposite direction

AAPL: BUY 50 Shares @ $100

AMZN: BUY 50 Shares @ $100

MSFT: BUY 50 Shares @ $100

After you have bought these stocks you can try selling them as well. To ensure that the sells are filled you will have to sell for very cheap: we recommend $1.

AAPL: SELL 10 Shares @ $1

AMZN: SELL 10 Shares @ $1

MSFT: SELL 10 Shares @ $1

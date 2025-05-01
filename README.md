# trading-platform

A trading platform for our group assignment (Software Engineering Tools Techniques and Practices)

- Each separate microservice should be created in apps/ with its own pom.xml
- Any shared libraries / utilities can be put in packages/
- scripts/ is for CICD scripts

## To run the app

`docker compose up`

The UI is hosted on `http://localhost:5173/`

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

### For development

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

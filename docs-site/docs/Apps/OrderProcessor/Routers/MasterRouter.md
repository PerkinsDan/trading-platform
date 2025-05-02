# Master Router

**Filename**: `MasterRouter.java`

## What does it do

`MasterRouter` constructs the root Vert.x `Router` for the application, then creates sub‑routers:

- **`/orders/*`** → `OrdersRouter`
- **`/users/*`** → `UsersRouter`

This centralises route creation so the HTTP server only needs to mount a single router.

## Methods

### `MasterRouter(Vertx vertx)`

- Constructor:
  1. Creates the root `Router` via `Router.router(vertx)`.
  2. Instantiates `OrdersRouter` and attaches it to `/orders/*`.
  3. Instantiates `UsersRouter` and attaches it to `/users/*`.

---

### `getRouter()`

- Returns: the fully configured root `Router`, ready to be supplied to `HttpServer.requestHandler()`.

# Mongo Client Connection

Filename: `MongoClientConnection.java`

## What does it do

`MongoClientConnection` is a singleton-style class that

- Reads the MongoDB connection string from the `DB_URI` environment variable.
- Builds a `MongoClient` for Server API v1.
- Connects to the tradingPlatform database and verifies the link with a `ping`.
- Provides a method for retrieving collections.

## Methods

### `createConnection()`

- Purpose: Ensures that the driver is initialised once.
- Behaviour:
  1. If `mongoClient` is `null`, invokes `initClient()`.
  2. Returns `true` to signal that a (shared) connection is now ready.

---

### `getCollection(String collection)`

- Parameters: `collection` - the name of the desired collection.
- Returns: `MongoCollection<Document>` bound to the current database.

---

### `initClient()` <sub>(private)</sub>

- Pulls `DB_URI` from the .env; throws an `IllegalStateException` if absent.
- Builds `MongoClientSettings` with `ServerApiVersion.V1`.
- Creates the `MongoClient`, selects the tradingPlatform database, and issues a `ping`.
- Logs _“Successfully connected to MongoDB!”_ on success or prints the stack‑trace on failure.

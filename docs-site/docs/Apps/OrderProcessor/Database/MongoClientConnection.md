# Mongo Client Connection

Filename: `MongoClientConnection.java`

## What does it do

`MongoClientConnection` is a singleton-style class that

- Reads the MongoDB connection string from the `DB_URI` environment variable.
- Connects to the tradingPlatform database and verifies the link with a `ping`.
- Provides a method for retrieving collections.

## Methods

### `createConnection()`

- Purpose: Ensures that the driver is initialised once.
- Behaviour:
  1. If `mongoClient` is `null`, calls `initClient()`.
  2. Returns `true` to signal that a (shared) connection is now ready.

---

### `getCollection(String collection)`

- Parameters: `collection` - the name of the collection.
- Returns: `MongoCollection<Document>` representing the collection in the database

---

### `initClient()` <sub>(private)</sub>

- Pulls `DB_URI` from the .env; throws an `IllegalStateException` if absent.
- Creates the `MongoClient`, selects the tradingPlatform database, and issues a `ping`.
- Logs “Successfully connected to MongoDB!” on success or prints the stack‑trace on failure.

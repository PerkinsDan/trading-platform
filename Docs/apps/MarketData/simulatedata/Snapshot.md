**What does it do?**

Snapshot is a record class, a record is a special stype of class designed
to be an immutable data carrier. So Snapshot doesnt change after its been created

<br>

**Where do we use it?**

Snapshot is the base unit for market data in this application, it holds a timestamp, a price for that specific moment in time, and the change from the previous price.

<br>

**Attributes**

* price : price for the stock at a partcular moment in time
* timestamp : timestamp to specify what moment in time the price is for
* change : change in price from previous Snapshot.

<br>

**Methods**

Records have public 'getters' but they are named after the attribute themselves rather than `getAttribute()` like in convention.
* price() : return price of the Snapshot
* timestamp() : returns timestamp of the Snapshot
* change() : returns change of the Snapshot
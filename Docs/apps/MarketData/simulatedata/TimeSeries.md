What does it do?
    The TimeSeries class represents a collection of time-based data snapshots.
    It provides functionality to manage and retrieve snapshots based on their timestamps.
    This class is useful for managing time-series data and ensuring that only relevant
    snapshots are considered based on the current time.
 
When do we use it?
    Used by the simulateData which aggreagates Timeseries into its timeSeriesMap
    Attributes:
    snapshots: A list that stores instances of the Snapshot class. Each Snapshot represents
               a data point with an associated timestamp.

Methods:
    TimeSeries(): Constructor that initializes the `snapshots` list.
                  addSnapshot(Snapshot snapshot)`: Adds a new Snapshot to the `snapshots` list.
    addSnapshot(Snapshot) : appends a new Snaphot to the end of the 'snaphots' list.
    getSnapshots(): Returns a filtered list of Snapshots whose timestamps are not in the future.
                    This ensures that only valid snapshots up to the current time are returned.
    getLatestSnapshot(): Retrieves the most recent Snapshot from the `snapshots` list whose timestamp
                         is before the current time. Returns `null` if no valid snapshots exist.

 
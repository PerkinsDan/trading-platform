import { useEffect, useState } from "react";
import { Snapshot, Ticker } from "../../types";
import { LATEST_SNAPSHOT_ENDPOINT } from "../constants/endpoints";

const useFetchSnapshots = () => {
  //   const [marketSnapshots, setMarketSnapshots] = useState<Snapshot[]>([]);
  const [marketSnapshots, setMarketSnapshots] =
    useState<Map<Ticker, Snapshot>>();

  useEffect(() => {
    const getTickerData = async () => {
      const responses = await Promise.all(
        Object.values(Ticker).map((ticker) => {
          return fetch(LATEST_SNAPSHOT_ENDPOINT + ticker);
        }),
      );

      const data = await Promise.all(
        responses.map((response) => response.json()),
      );

      const snapshotMap = new Map<Ticker, Snapshot>();
      Object.values(Ticker).forEach((ticker, index) => {
        snapshotMap.set(ticker as Ticker, data[index]);
      });

      setMarketSnapshots(snapshotMap);
    };

    getTickerData();
  }, []);

  return marketSnapshots;
};

export default useFetchSnapshots;

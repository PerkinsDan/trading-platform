import { Line } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  TooltipItem,
} from "chart.js";
import { Stack } from "@mui/material";
import { Ticker } from "../../types";

// Register Chart.js components
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
);
interface TradingGraphProps {
  timeSeries: number[];
  timestamps: string[];
  stock: Ticker;
}

function TradeGraph({ timeSeries, timestamps, stock }: TradingGraphProps) {
  const chartData = {
    labels: timestamps,
    datasets: [
      {
        label: `${stock} Price`,
        data: timeSeries,
        borderColor: "rgba(75, 192, 192, 1)",
        backgroundColor: "rgba(75, 192, 192, 0.2)",
        tension: 0.4,
      },
    ],
  };

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false,
      },
      tooltip: {
        callbacks: {
          label: (context: TooltipItem<"line">) => {
            const value = context.raw;
            return `$${value}`;
          },
        },
      },
    },
    scales: {
      x: {
        ticks: {
          maxTicksLimit: 5,
        },
      },
      y: {
        beginAtZero: false,
        ticks: {
          callback: (value: string | number) => `$${value}`,
        },
      },
    },
  };
  return (
    <Stack padding="1rem" flex={1}>
      <Line data={chartData} height="100%" options={chartOptions} />
    </Stack>
  );
}

export default TradeGraph;

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
} from "chart.js";
import { Stack } from "@mui/material";

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
  stock: string;
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
          label: (context: any) => {
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
    <Stack flex={1} padding="1rem">
      <Line data={chartData} options={chartOptions} />
    </Stack>
  );
}

export default TradeGraph;

import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import SignIn from "./pages/SignIn";
import MakeTrades from "./pages/MakeTrades";
import MyTrades from "./pages/MyTrades";
import ProtectedRoute from "./components/Routes/ProtectedRoute";
import UnprotectedRoute from "./components/Routes/UnprotectedRoute";
import TickerTrades from "./pages/TickerTrades";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="make-trades">
          <Route
            index
            element={
              <ProtectedRoute>
                <MakeTrades />
              </ProtectedRoute>
            }
          />
          <Route
            path=":ticker"
            element={
              <ProtectedRoute>
                <TickerTrades />
              </ProtectedRoute>
            }
          />
        </Route>
        <Route
          path="my-trades"
          element={
            <ProtectedRoute>
              <MyTrades />
            </ProtectedRoute>
          }
        />
        <Route
          path="sign-in"
          element={
            <UnprotectedRoute>
              <SignIn />
            </UnprotectedRoute>
          }
        />
        <Route
          path="*"
          element={
            <ProtectedRoute>
              <MyTrades />
            </ProtectedRoute>
          }
        />
      </Routes>
    </Router>
  );
}

export default App;

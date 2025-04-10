import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import SignIn from "./pages/SignIn";
import MakeTrades from "./pages/MakeTrades";
import MyTrades from "./pages/MyTrades";
import ProtectedRoute from "./components/ProtectedRoute";
import UnprotectedRoute from "./components/UnprotectedRoute";

function App() {
  return (
    <Router>
      <Routes>
        <Route
          path="make-trades"
          element={
            <ProtectedRoute>
              <MakeTrades />
            </ProtectedRoute>
          }
        />
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

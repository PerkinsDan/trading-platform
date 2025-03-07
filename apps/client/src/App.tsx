import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import SignIn from "./pages/SignIn";

const App: React.FC = () => {
  return (
    <Router>
      <Routes>
        <Route path="*" element={<SignIn />} />
      </Routes>
    </Router>
  );
};

export default App;

import { Auth, getAuth } from "firebase/auth";
import { initializeApp } from "firebase/app";

const firebaseConfig = {
  apiKey:
    import.meta.env.VITE_APP_FIREBASE_API_KEY ||
    process.env.VITE_APP_FIREBASE_API_KEY,
  authDomain: "tradingplatform-bc1a7.firebaseapp.com",
  projectId: "tradingplatform-bc1a7",
  storageBucket: "tradingplatform-bc1a7.firebasestorage.app",
  messagingSenderId: "657364436425",
  appId: "1:657364436425:web:7a92b5758104a7972fd418",
  measurementId: "G-GGS7RV3PCV",
};

let auth: Auth | undefined;
try {
  const app = initializeApp(firebaseConfig);
  auth = getAuth(app);
} catch (error) {
  console.error("Error initializing Firebase:", error);
}

export { auth };

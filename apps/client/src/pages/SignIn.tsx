import { Button, Container, Typography, Box } from "@mui/material";
import { GoogleAuthProvider, signInWithPopup } from "firebase/auth";
import { auth } from "../firebaseConfig/firebase";
import { useNavigate } from "react-router-dom";
import { CREATE_USER_ENDPOINT } from "../constants/endpoints";

function SignIn() {
  const navigate = useNavigate();

  const handleGoogleSignIn = async () => {
    const provider = new GoogleAuthProvider();
    provider.addScope("profile");
    provider.addScope("email");
    try {
      const result = await signInWithPopup(auth, provider);
      const user = result.user;
      const token = await user.getIdToken();
      const userData = {
        displayName: user.displayName,
        email: user.email,
        photoURL: user.photoURL,
        uid: user.uid,
      };

      // API call to add/get user
      const response = await fetch(
        CREATE_USER_ENDPOINT,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({ userId: userData.uid }),
        },
      );

      if (!response.ok) {
        throw new Error("Failed to add/get user from the database");
      }

      console.log("User added/retrieved successfully:", userData);
      navigate("/my-trades"); // Redirect after successful sign-in
    } catch (error) {
      console.error("Error signing in with Google:", error);
    }
  };

  return (
    <Container>
      <Box
        display="flex"
        flexDirection="column"
        justifyContent="center"
        alignItems="center"
        height="100vh"
        textAlign="center"
      >
        <Typography variant="h4" component="h1" gutterBottom>
          Sign In
        </Typography>
        <Button
          variant="contained"
          color="primary"
          onClick={handleGoogleSignIn}
        >
          Sign in with Google
        </Button>
      </Box>
    </Container>
  );
}

export default SignIn;

import React from "react";
import { Button, Container, Typography, Box } from "@mui/material";
import { GoogleAuthProvider, signInWithPopup } from "firebase/auth";
import { auth } from "../firebaseConfig/firebase";

const SignIn: React.FC = () => {
  const handleGoogleSignIn = async () => {
    const provider = new GoogleAuthProvider();
    try {
      await signInWithPopup(auth, provider);
      // TODO: api call createUser
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
};

export default SignIn;

package com.example.eventconnect.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventconnect.R

@Composable
fun LoginScreen(
    onNavigateToSignup: () -> Unit,
    onLoginSuccess: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState == "SUCCESS") {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6EED2)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(40.dp))
            Image(
                painter = painterResource(R.drawable.eventeats_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(70.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Login",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(Modifier.height(32.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Enter your Email ID") },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Enter your password") },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF6EED2), RoundedCornerShape(16.dp))
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { authViewModel.login(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B7C6F)
                )
            ) {
                Text("Login", fontSize = 18.sp, color = Color.White)
            }
            Spacer(Modifier.height(16.dp))
            TextButton(onClick = onNavigateToSignup) {
                Text(
                    "Already have an account ? Signup",
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
            authState?.let {
                if (it != "SUCCESS") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

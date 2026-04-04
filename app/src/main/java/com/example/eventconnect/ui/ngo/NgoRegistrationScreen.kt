package com.example.eventconnect.ui.ngo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventconnect.data.network.NGOCreateRequest
import com.example.eventconnect.data.network.RetrofitClient
import com.example.eventconnect.ui.auth.getFirebaseIdToken
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NgoRegistrationScreen(navController: NavController) {

    var name by remember { mutableStateOf("") }
    var regNo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Theme Colors
    val bgColor = Color(0xFFF8FAFC)
    val cardBg = Color(0xFFFFFFFF)
    val primaryPurple = Color(0xFF6C3EF4)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)
    val inputBg = Color(0xFFF1F5F9)
    val gradientPurple = Brush.linearGradient(
        colors = listOf(Color(0xFF7C3AED), Color(0xFF6C3EF4))
    )

    Scaffold(
        containerColor = bgColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            
            Spacer(modifier = Modifier.height(40.dp))

            // --- HEADER ---
            Text(
                text = "NGO Registration",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                ),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Register your NGO to receive surplus food",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = textSecondary
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // --- FORM CONTAINER ---
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0x1A000000)),
                shape = RoundedCornerShape(20.dp),
                color = cardBg
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // NGO NAME FIELD
                    Text(
                        text = "NGO Name",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = textPrimary
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        placeholder = { Text("Enter NGO Name", color = textSecondary) },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = primaryPurple) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = inputBg,
                            unfocusedContainerColor = inputBg,
                            disabledContainerColor = inputBg,
                            focusedIndicatorColor = primaryPurple,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // REGISTRATION NUMBER FIELD
                    Text(
                        text = "Registration Number",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = textPrimary
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    TextField(
                        value = regNo,
                        onValueChange = { regNo = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        placeholder = { Text("Enter Registration Number", color = textSecondary) },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = primaryPurple) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = inputBg,
                            unfocusedContainerColor = inputBg,
                            disabledContainerColor = inputBg,
                            focusedIndicatorColor = primaryPurple,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- REGISTER BUTTON ---
                    Button(
                        onClick = {
                            if (name.isBlank() || regNo.isBlank()) {
                                error = "Please fill all fields"
                                return@Button
                            }
                            isLoading = true
                            error = null
                            getFirebaseIdToken(
                                onTokenReceived = { token ->
                                    scope.launch {
                                        try {
                                            val res = RetrofitClient.apiService.registerNgo(
                                                "Bearer $token",
                                                NGOCreateRequest(name, regNo)
                                            )

                                            if (res.isSuccessful) {
                                                navController.navigate("ngo-documents") {
                                                    popUpTo("ngo-register") { inclusive = true }
                                                }
                                            } else {
                                                error = "Registration failed. Please try again."
                                            }
                                        } catch (e: Exception) {
                                            error = "An error occurred: ${e.localizedMessage}"
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                },
                                onError = { 
                                    error = it
                                    isLoading = false
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp), clip = false),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        enabled = !isLoading
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(gradientPurple, shape = RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    "Register NGO",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // --- ERROR MESSAGE ---
                    error?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

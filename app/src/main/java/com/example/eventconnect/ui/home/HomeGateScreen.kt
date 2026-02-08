package com.example.eventconnect.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.eventconnect.data.network.RetrofitClient
import com.example.eventconnect.ui.auth.getFirebaseIdToken
import kotlinx.coroutines.launch

@Composable
fun HomeGateScreen(navController: NavController) {

    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf("Checking user role...") }

    LaunchedEffect(Unit) {
        getFirebaseIdToken(
            onTokenReceived = { token ->
                scope.launch {
                    try {
                        val response = RetrofitClient.apiService.protectedCall(
                            token = "Bearer $token"
                        )

                        if (!response.isSuccessful) {
                            status = "Auth failed: \${response.code()} \${response.errorBody()?.string()}"
                            return@launch
                        }

                        val body = response.body()
                        val role = body?.get("role")?.toString()

                        when (role) {
                            null, "UNASSIGNED" -> {
                                navController.navigate("role-selection") {
                                    popUpTo("home-gate") { inclusive = true }
                                }
                            }

                            "event_organizer" -> {
                                navController.navigate("organizer-home") {
                                    popUpTo("home-gate") { inclusive = true }
                                }
                            }

                            "caterer" -> {
                                navController.navigate("caterer-home") {
                                    popUpTo("home-gate") { inclusive = true }
                                }
                            }

                            "ngo" -> {
                                navController.navigate("ngo-home") {
                                    popUpTo("home-gate") { inclusive = true }
                                }
                            }

                            else -> {
                                status = "Unknown role: $role"
                            }
                        }
                    } catch (e: Exception) {
                        status = "Error: ${e.message}"
                    }
                }
            },
            onError = { error ->
                status = error
            }
        )
    }

    // 👇 THIS PREVENTS WHITE SCREEN
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(status)
    }
}

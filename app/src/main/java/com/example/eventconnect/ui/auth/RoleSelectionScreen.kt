package com.example.eventconnect.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eventconnect.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RoleSelectionScreen(
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    var error by remember { mutableStateOf<String?>(null) }

    fun selectRole(role: String, route: String) {
        getFirebaseIdToken(
            onTokenReceived = { token ->

                // 🔥 DEBUG (remove later)
                println("🔥 Firebase Token: $token")

                if (token.isBlank()) {
                    error = "Empty auth token"
                    return@getFirebaseIdToken
                }

                scope.launch(Dispatchers.IO) {
                    try {
                        val response = RetrofitClient.apiService.selectRole(
                            role = role,
                            token = "Bearer $token"
                        )

                        if (response.isSuccessful) {
                            withContext(Dispatchers.Main) {
                                navController.navigate(route) {
                                    popUpTo("role-selection") { inclusive = true }
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                error = "Failed: ${response.code()}"
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            error = e.message
                        }
                    }
                }
            },
            onError = {
                error = it
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Select Your Role", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { selectRole("event_organizer", "organizer-home") },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Event Organizer") }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { selectRole("caterer", "caterer-home") },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Caterer") }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { selectRole("ngo", "ngo-home") },
            modifier = Modifier.fillMaxWidth()
        ) { Text("NGO") }

        error?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}

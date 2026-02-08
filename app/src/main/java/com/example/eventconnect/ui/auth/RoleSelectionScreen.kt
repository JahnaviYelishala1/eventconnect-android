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

@Composable
fun RoleSelectionScreen(
    navController: NavController
) {
    val scope = rememberCoroutineScope()

    fun selectRole(role: String, route: String) {
        getFirebaseIdToken(
            onTokenReceived = { token ->
                scope.launch(Dispatchers.IO) {
                    val response = RetrofitClient.apiService.selectRole(
                        role = role,
                        token = "Bearer $token"
                    )

                    if (response.isSuccessful) {
                        navController.navigate(route) {
                            popUpTo("role-selection") { inclusive = true }
                        }
                    }
                }
            },
            onError = {}
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
        ) {
            Text("Event Organizer")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { selectRole("caterer", "caterer-home") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Caterer")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { selectRole("ngo", "ngo-home") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("NGO")
        }
    }
}

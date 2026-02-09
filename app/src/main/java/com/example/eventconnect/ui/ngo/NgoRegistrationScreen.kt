package com.example.eventconnect.ui.ngo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eventconnect.data.network.NGOCreateRequest
import com.example.eventconnect.data.network.RetrofitClient
import com.example.eventconnect.ui.auth.getFirebaseIdToken
import kotlinx.coroutines.launch

@Composable
fun NgoRegistrationScreen(navController: NavController) {

    var name by remember { mutableStateOf("") }
    var regNo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("NGO Registration", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("NGO Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = regNo,
            onValueChange = { regNo = it },
            label = { Text("Registration Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                getFirebaseIdToken(
                    onTokenReceived = { token ->
                        scope.launch {
                            val res = RetrofitClient.apiService.registerNgo(
                                "Bearer $token",
                                NGOCreateRequest(name, regNo)
                            )

                            if (res.isSuccessful) {
                                navController.navigate("ngo-documents") {
                                    popUpTo("ngo-register") { inclusive = true }
                                }
                            } else {
                                error = "Registration failed"
                            }
                        }
                    },
                    onError = { error = it }
                )
            }
        ) {
            Text("Register NGO")
        }

        error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}

package com.example.eventconnect.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eventconnect.data.network.RetrofitClient
import com.example.eventconnect.ui.auth.getFirebaseIdToken
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NgoHomeScreen(navController: NavController) {

    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("PENDING") }

    LaunchedEffect(Unit) {
        getFirebaseIdToken(
            onTokenReceived = { token ->
                scope.launch {

                    // 1️⃣ Get NGO meta info
                    val ngoRes = RetrofitClient.apiService.getMyNgo(
                        token = "Bearer $token"
                    )

                    if (!ngoRes.isSuccessful || ngoRes.body() == null) {
                        navController.navigate("ngo-register") {
                            popUpTo("ngo-home") { inclusive = true }
                        }
                        return@launch
                    }

                    val ngo = ngoRes.body()!!

                    // 🔴 NGO not registered
                    if (!ngo.exists) {
                        navController.navigate("ngo-register") {
                            popUpTo("ngo-home") { inclusive = true }
                        }
                        return@launch
                    }

                    status = ngo.status ?: "PENDING"

                    // 2️⃣ Documents uploaded?
                    if (!ngo.documents_uploaded) {
                        navController.navigate("ngo-documents") {
                            popUpTo("ngo-home") { inclusive = true }
                        }
                        return@launch
                    }

                    // 3️⃣ Status message
                    message = when (status) {
                        "PENDING" -> "🕒 Documents under verification"
                        "REJECTED" -> "❌ NGO verification rejected"
                        "SUSPENDED" -> "⛔ NGO account suspended"
                        "VERIFIED" -> "✅ NGO Verified. You can now accept food."
                        else -> "Unknown NGO status"
                    }

                    loading = false
                }
            },
            onError = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }

    // 🔄 LOADING
    if (loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // 🧱 UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NGO Dashboard") },
                actions = {
                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, "Logout")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.List, null) },
                    label = { Text("Status") }
                )

                NavigationBarItem(
                    selected = false,
                    enabled = status == "VERIFIED",
                    onClick = {
                        navController.navigate("ngo-profile")
                    },
                    icon = { Icon(Icons.Default.Check, null) },
                    label = { Text("Profile") }
                )
            }
        }

    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.padding(24.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("NGO Status", style = MaterialTheme.typography.titleLarge)
                    Divider(Modifier.padding(vertical = 12.dp))
                    Text(message)
                }
            }
        }
    }
}

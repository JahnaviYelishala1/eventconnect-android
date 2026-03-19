package com.example.eventconnect.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventconnect.data.network.RetrofitClient
import com.example.eventconnect.ui.auth.getFirebaseIdToken
import com.example.eventconnect.websocket.NgoWebSocketManager
import com.example.eventconnect.websocket.SurplusAlert
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NgoHomeScreen(navController: NavController) {

    val scope = rememberCoroutineScope()

    var loading by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("PENDING") }

    var alert by remember { mutableStateOf<SurplusAlert?>(null) }
    var firebaseToken by remember { mutableStateOf<String?>(null) }

    // var socketConnected by remember { mutableStateOf(false) } // Not needed, value is never read

    // ---------------- GET NGO STATUS ----------------

    LaunchedEffect(Unit) {
        getFirebaseIdToken(
            onTokenReceived = { token ->
                firebaseToken = token

                scope.launch {
                    try {

                        val ngoRes = RetrofitClient.apiService.getMyNgo("Bearer $token")

                        if (!ngoRes.isSuccessful || ngoRes.body() == null) {
                            navController.navigate("ngo-register") {
                                popUpTo("ngo-home") { inclusive = true }
                            }
                            return@launch
                        }

                        val ngo = ngoRes.body()!!

                        if (!ngo.exists) {
                            navController.navigate("ngo-register") {
                                popUpTo("ngo-home") { inclusive = true }
                            }
                            return@launch
                        }

                        status = ngo.status ?: "PENDING"

                        if (!ngo.documents_uploaded) {
                            navController.navigate("ngo-documents") {
                                popUpTo("ngo-home") { inclusive = true }
                            }
                            return@launch
                        }

                        message = when (status) {
                            "PENDING" -> "🕒 Documents under verification"
                            "REJECTED" -> "❌ NGO verification rejected"
                            "SUSPENDED" -> "⛔ NGO account suspended"
                            "VERIFIED" -> "✅ NGO Verified. Waiting for food alerts..."
                            else -> "Unknown NGO status"
                        }

                    } finally {
                        loading = false
                    }
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

    // ---------------- CONNECT WEBSOCKET ----------------

    LaunchedEffect(status, firebaseToken) {

        if (status == "VERIFIED" && firebaseToken != null) {

            val manager = NgoWebSocketManager(firebaseToken!!) { surplusAlert ->
                alert = surplusAlert
            }

            manager.connect()
        }
    }

    // ---------------- UI ----------------

    Scaffold(
        containerColor = Color(0xFFFAF8F0),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = {
                    Text(
                        "NGO Dashboard",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, "Logout", tint = Color.Black)
                    }
                }
            )
        }
    ) { padding ->

        if (loading) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // -------- NGO STATUS CARD --------

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {

                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        "NGO Status",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Text(
                        message,
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    )
                }
            }

            // -------- SURPLUS ALERT CARD --------

            alert?.let {
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "\uD83D\uDEA8 Surplus Food Available",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Event: ${it.eventName}")
                        Text("Food: ${it.foodDescription}")
                        Text("Distance: ${it.distance} km")
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        val token = firebaseToken ?: return@launch
                                        RetrofitClient.apiService.acceptSurplus(
                                            "Bearer $token",
                                            it.requestId
                                        )
                                        alert = null
                                        navController.navigate("ngo-accepted-requests")
                                    }
                                }
                            ) {
                                Text("Accept")
                            }
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        val token = firebaseToken ?: return@launch
                                        RetrofitClient.apiService.rejectSurplus(
                                            "Bearer $token",
                                            it.requestId
                                        )
                                        alert = null
                                    }
                                }
                            ) {
                                Text("Reject")
                            }
                        }
                    }
                }
            }
        }
    }
}
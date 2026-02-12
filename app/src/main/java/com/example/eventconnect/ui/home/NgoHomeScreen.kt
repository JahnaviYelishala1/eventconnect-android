package com.example.eventconnect.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventconnect.R
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
                    try {
                        val ngoRes = RetrofitClient.apiService.getMyNgo("Bearer $token")

                        if (!ngoRes.isSuccessful || ngoRes.body() == null) {
                            navController.navigate("ngo-register") { popUpTo("ngo-home") { inclusive = true } }
                            return@launch
                        }

                        val ngo = ngoRes.body()!!

                        if (!ngo.exists) {
                            navController.navigate("ngo-register") { popUpTo("ngo-home") { inclusive = true } }
                            return@launch
                        }

                        status = ngo.status ?: "PENDING"

                        if (!ngo.documents_uploaded) {
                            navController.navigate("ngo-documents") { popUpTo("ngo-home") { inclusive = true } }
                            return@launch
                        }

                        message = when (status) {
                            "PENDING" -> "🕒 Documents under verification"
                            "REJECTED" -> "❌ NGO verification rejected"
                            "SUSPENDED" -> "⛔ NGO account suspended"
                            "VERIFIED" -> "✅ NGO Verified. You can now accept food."
                            else -> "Unknown NGO status"
                        }
                    } finally {
                        loading = false
                    }
                }
            },
            onError = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("login") { popUpTo(0) { inclusive = true } }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFFAF8F0),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text("NGO Dashboard", color = Color.Black, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    }) {
                        Icon(Icons.Default.ExitToApp, "Logout", tint = Color.Black)
                    }
                }
            )
        }
    ) { padding ->
        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                    Text("NGO Status", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    Text(message, fontSize = 16.sp, color = Color.DarkGray)
                }
            }
        }
    }
}

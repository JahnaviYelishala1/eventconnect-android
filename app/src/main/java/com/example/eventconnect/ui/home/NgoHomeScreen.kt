package com.example.eventconnect.ui.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- MODERN UI COLORS ---
private val ColorGradientStart = Color(0xFFFFFFFF)
private val ColorGradientEnd = Color(0xFFF5F3FF) // Light lavender
private val ColorCardWhite = Color(0xFFFFFFFF)
private val ColorTextPrimary = Color(0xFF1A1C1E)
private val ColorTextSecondary = Color(0xFF6B7280)
private val ColorTextContent = Color(0xFF111827)
private val ColorPurpleAccent = Color(0xFF6C3EF4)
private val ColorGreenSuccess = Color(0xFF22C55E)
private val ColorRedError = Color(0xFFEF4444)
private val ColorGreenTint = Color(0xFFECFDF5)
private val ColorDivider = Color(0xFFE5E7EB)
private val ColorStatusArea = Color(0xFFF0FDF4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NgoHomeScreen(navController: NavController) {

    val scope = rememberCoroutineScope()

    var loading by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("PENDING") }

    var alert by remember { mutableStateOf<SurplusAlert?>(null) }
    var firebaseToken by remember { mutableStateOf<String?>(null) }

    // ---------------- LOGIC: STATUS FETCHING ----------------

    LaunchedEffect(Unit) {
        while(true) {
            getFirebaseIdToken(
                onTokenReceived = { token ->
                    firebaseToken = token
                    Log.d("NGO_HOME", "Token refreshed")
                    
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
                                "VERIFIED" -> "NGO Verified. Waiting for food alerts"
                                else -> "Unknown NGO status"
                            }

                        } catch (e: Exception) {
                            Log.e("NGO_HOME", "Error fetching NGO status: ${e.message}")
                        } finally {
                            loading = false
                        }
                    }
                },
                onError = {
                    Log.e("NGO_HOME", "Auth error: $it")
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
            delay(300000) // 5 minutes
        }
    }

    // ---------------- LOGIC: WEBSOCKET ----------------

    DisposableEffect(status, firebaseToken) {
        var manager: NgoWebSocketManager? = null
        if (status == "VERIFIED" && firebaseToken != null) {
            Log.d("NGO_HOME", "Connecting WebSocket...")
            manager = NgoWebSocketManager(firebaseToken!!) { surplusAlert ->
                alert = surplusAlert
            }
            manager.connect()
        }
        onDispose { manager?.disconnect() }
    }

    // ---------------- UI DESIGN ----------------

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(ColorGradientStart, ColorGradientEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // --- TOP SECTION ---
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NGO Dashboard",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ColorTextPrimary
                )
                IconButton(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(ColorPurpleAccent.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout",
                        tint = ColorPurpleAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ColorPurpleAccent, strokeWidth = 3.dp)
                }
            } else {
                // --- NGO STATUS CARD ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(20.dp), ambientColor = Color.LightGray),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ColorCardWhite)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "NGO Status",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = ColorTextPrimary
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = ColorDivider,
                            thickness = 1.dp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (status == "VERIFIED") ColorStatusArea else Color.Transparent)
                                .padding(vertical = 12.dp, horizontal = 12.dp)
                        ) {
                            if (status == "VERIFIED") {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = ColorGreenSuccess,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                            Text(
                                text = message,
                                fontSize = 15.sp,
                                color = Color(0xFF374151),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- SURPLUS FOOD ALERT CARD ---
                alert?.let {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(12.dp, RoundedCornerShape(20.dp), ambientColor = ColorGreenSuccess.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = ColorGreenTint)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Fastfood,
                                    contentDescription = null,
                                    tint = ColorGreenSuccess,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Surplus Food Available",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = ColorGreenSuccess
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            Text(
                                text = it.eventName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = ColorTextContent
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = it.foodDescription,
                                fontSize = 15.sp,
                                color = ColorTextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = ColorTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${it.distance} km away",
                                    fontSize = 13.sp,
                                    color = ColorTextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(28.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            val token = firebaseToken ?: return@launch
                                            try {
                                                val res = RetrofitClient.apiService.acceptSurplus("Bearer $token", it.requestId)
                                                if (res.isSuccessful) {
                                                    alert = null
                                                    navController.navigate("ngo-accepted-requests")
                                                }
                                            } catch (e: Exception) { Log.e("NGO_HOME", "Accept error: ${e.message}") }
                                        }
                                    },
                                    modifier = Modifier.weight(1f).height(52.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ColorGreenSuccess),
                                    shape = RoundedCornerShape(26.dp),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                                ) {
                                    Text("Accept", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        scope.launch {
                                            val token = firebaseToken ?: return@launch
                                            try {
                                                RetrofitClient.apiService.rejectSurplus("Bearer $token", it.requestId)
                                                alert = null
                                            } catch (e: Exception) { Log.e("NGO_HOME", "Reject error: ${e.message}") }
                                        }
                                    },
                                    modifier = Modifier.weight(1f).height(52.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorRedError),
                                    border = androidx.compose.foundation.BorderStroke(2.dp, ColorRedError),
                                    shape = RoundedCornerShape(26.dp)
                                ) {
                                    Text("Reject", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                } ?: run {
                    // --- EMPTY STATE ---
                    Box(
                        modifier = Modifier.fillMaxSize().padding(bottom = 100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "✨ No active alerts",
                                color = ColorTextSecondary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                                )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "We'll notify you when food is nearby.",
                                color = ColorTextSecondary.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

package com.example.eventconnect.ui.ngo

import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eventconnect.data.network.AcceptedRequestResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

// --- MODERN LIGHT THEME PALETTE ---
private val ColorBgStart = Color(0xFFFFFFFF)
private val ColorBgEnd = Color(0xFFF5F3FF) // Light Lavender
private val ColorTextPrimary = Color(0xFF1A1C1E)
private val ColorTextSecondary = Color(0xFF6B7280)
private val ColorTextContent = Color(0xFF111827)
private val ColorPurple = Color(0xFF6C3EF4)
private val ColorGreen = Color(0xFF22C55E)
private val ColorCardWhite = Color(0xFFFFFFFF)
private val ColorBorderGray = Color(0xFFD1D5DB)
private val ColorActionDark = Color(0xFF374151)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcceptedRequestsScreen(
    viewModel: AcceptedRequestsViewModel = viewModel(),
    navController: NavController
) {
    val requests by viewModel.requests.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadRequests()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(ColorBgStart, ColorBgEnd)))
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Current Accepted Requests",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ColorTextPrimary
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { padding ->
            if (requests.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "✨ No active requests",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Accepted food pickups will appear here.",
                            fontSize = 14.sp,
                            color = ColorTextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(requests) { request ->
                        AcceptedRequestCard(request, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun AcceptedRequestCard(
    request: AcceptedRequestResponse,
    navController: NavController
) {
    val context = LocalContext.current
    var address by remember { mutableStateOf("Loading address...") }
    
    LaunchedEffect(request.latitude, request.longitude) {
        address = getAddressFromLatLng(context, request.latitude, request.longitude)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(20.dp), ambientColor = Color.LightGray),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ColorCardWhite)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header Row: Icon + Event Name + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fastfood,
                        contentDescription = null,
                        tint = ColorPurple,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = request.event_name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorTextPrimary,
                        maxLines = 1
                    )
                }
                
                // Status Pill
                Surface(
                    color = ColorGreen.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Accepted",
                        color = ColorGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Organizer Info
            Text(
                text = "Organizer: ${request.organizer_name}",
                fontSize = 14.sp,
                color = ColorTextSecondary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Food Details
            Text(
                text = request.food_description,
                fontSize = 16.sp,
                color = ColorTextContent,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(18.dp))

            // Pickup Address
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = ColorTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = address,
                    fontSize = 14.sp,
                    color = ColorTextSecondary,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Requested Time
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = ColorTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = request.created_at,
                    fontSize = 13.sp,
                    color = ColorTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Chat Button
                Button(
                    onClick = { navController.navigate("chat/${request.request_id}") },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorPurple),
                    shape = RoundedCornerShape(25.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Chat", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                // Call Button
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL)
                        intent.data = Uri.parse("tel:${request.organizer_phone}")
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, ColorBorderGray),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp), tint = ColorActionDark)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Call", color = ColorActionDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

suspend fun getAddressFromLatLng(context: android.content.Context, lat: Double, lng: Double): String {
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val lines = (0..address.maxAddressLineIndex).map { address.getAddressLine(it) }
                lines.joinToString(", ")
            } else {
                "Address not found"
            }
        } catch (e: Exception) {
            "Address not found"
        }
    }
}

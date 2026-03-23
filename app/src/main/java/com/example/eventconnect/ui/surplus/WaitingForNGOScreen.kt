package com.example.eventconnect.ui.surplus

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaitingForNGOScreen(
    navController: NavController,
    requestId: Int? = null,
    viewModel: SurplusViewModel = viewModel()
) {
    val ngo by viewModel.acceptedNgo.collectAsState()
    val context = LocalContext.current

    val darkBackground = Color(0xFF0F172A)
    val deepPurple = Color(0xFF1E1B4B)
    val surfaceDark = Color(0xFF1E293B)
    val textMuted = Color(0xFF94A3B8)
    val textBright = Color(0xFFF8FAFC)
    val primaryPurple = Color(0xFF7C3AED)
    val secondaryViolet = Color(0xFF8B5CF6)
    val successGreen = Color(0xFF10B981)

    LaunchedEffect(Unit) {
        if (requestId != null) {
            viewModel.requestId = requestId
            viewModel.fetchAcceptedNgoOnce(requestId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(darkBackground, deepPurple)))
    ) {
        // Glow effect
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.Center)
                .background(primaryPurple.copy(alpha = 0.15f), CircleShape)
                .blur(100.dp)
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { 
                        Text(
                            "Food Donation Status",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        ) 
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                if (ngo == null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(80.dp),
                            strokeWidth = 6.dp,
                            color = primaryPurple
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            "Searching for nearby NGOs...",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = textBright
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "We're notifying local organizations about your donation. Please stay on this screen.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = textMuted
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + expandVertically()
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                                .shadow(24.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black),
                            shape = RoundedCornerShape(24.dp),
                            color = surfaceDark
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Modern Rounded Icon Container
                                Surface(
                                    shape = CircleShape,
                                    color = successGreen.copy(alpha = 0.15f),
                                    modifier = Modifier.size(72.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = successGreen,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "NGO Matched!",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        fontSize = 26.sp
                                    )
                                )

                                Spacer(modifier = Modifier.height(28.dp))

                                // Info Sections
                                InfoRow(label = "Organization", value = ngo!!.ngo_name)
                                Spacer(modifier = Modifier.height(16.dp))
                                InfoRow(label = "Contact Person", value = ngo!!.phone)

                                Spacer(modifier = Modifier.height(36.dp))

                                // Primary Button: Navigate to NGO
                                Button(
                                    onClick = {
                                        viewModel.requestId?.let { id ->
                                            viewModel.fetchLocation(id) { lat, lng ->
                                                val uri = Uri.parse("google.navigation:q=$lat,$lng")
                                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                                intent.setPackage("com.google.android.apps.maps")
                                                try {
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    val fallbackUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng")
                                                    val fallbackIntent = Intent(Intent.ACTION_VIEW, fallbackUri)
                                                    context.startActivity(fallbackIntent)
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .shadow(12.dp, RoundedCornerShape(24.dp), ambientColor = primaryPurple),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    contentPadding = PaddingValues()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Brush.horizontalGradient(listOf(primaryPurple, secondaryViolet))),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Navigate to NGO", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Secondary Button: View NGO Location
                                OutlinedButton(
                                    onClick = {
                                        viewModel.requestId?.let { id ->
                                            viewModel.fetchLocation(id) { lat, lng ->
                                                navController.navigate("surplus-location/$lat/$lng")
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, primaryPurple.copy(alpha = 0.5f))
                                ) {
                                    Text("View NGO Location", color = textBright, fontWeight = FontWeight.Medium)
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Action Buttons Row
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    ModernActionButton(
                                        text = "Call",
                                        icon = Icons.Default.Call,
                                        containerColor = successGreen,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${ngo!!.phone}"))
                                            context.startActivity(intent)
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    ModernActionButton(
                                        text = "Chat",
                                        icon = Icons.Default.Chat,
                                        containerColor = Color(0xFF475569),
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            viewModel.requestId?.let { id ->
                                                navController.navigate("chat/$id")
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF94A3B8),
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF8FAFC)
        )
    }
}

@Composable
fun ModernActionButton(
    text: String,
    icon: ImageVector,
    containerColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        shape = RoundedCornerShape(20.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, fontWeight = FontWeight.Bold)
        }
    }
}

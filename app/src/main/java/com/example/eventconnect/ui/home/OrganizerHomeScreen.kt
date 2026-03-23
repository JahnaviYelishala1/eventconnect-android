package com.example.eventconnect.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun OrganizerHomeScreen(navController: NavController) {
    val lavender = Color(0xFFF9F7FF)
    val violet = Color(0xFFF1EAFF)
    val primaryPurple = Color(0xFF6C3EF4)
    val secondaryViolet = Color(0xFF9F5FFF)

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(lavender, violet)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        // Subtle animated glow effects for a premium feel
        AnimatedGlowEffect()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                DashboardTopBar(onLogout = {
                    try {
                        FirebaseAuth.getInstance().signOut()
                    } catch (e: Exception) {}
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                })
            }
            // Removed internal BottomBar as it is handled by the main NavGraph
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Stats Section with Glassmorphism
                StatsSection()

                Spacer(modifier = Modifier.height(32.dp))

                // Manage Events Action Card
                ActionCard(
                    title = "Manage Your Events",
                    subtitle = "View bookings and handle requests easily",
                    buttonText = "My Booking Requests",
                    onActionClick = {
                        navController.navigate("organizer-bookings")
                    }
                )
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun AnimatedGlowEffect() {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Canvas(modifier = Modifier
        .fillMaxSize()
        .blur(100.dp)) {
        drawCircle(
            color = Color(0xFF6C3EF4).copy(alpha = alpha),
            radius = 350.dp.toPx(),
            center = Offset(size.width * 0.9f, size.height * 0.15f)
        )
        drawCircle(
            color = Color(0xFF9F5FFF).copy(alpha = alpha * 0.8f),
            radius = 300.dp.toPx(),
            center = Offset(size.width * 0.1f, size.height * 0.85f)
        )
    }
}

@Composable
fun DashboardTopBar(onLogout: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        @Suppress("DEPRECATION")
        Text(
            text = "Organizer Dashboard",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1A1C1E),
            letterSpacing = (-0.5).sp
        )

        Surface(
            modifier = Modifier
                .size(46.dp)
                .shadow(8.dp, CircleShape),
            shape = CircleShape,
            color = Color.White
        ) {
            IconButton(onClick = onLogout) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Logout",
                    tint = Color(0xFF6C3EF4),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun StatsSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(label = "Total Events", value = "12", icon = Icons.Default.Event, modifier = Modifier.weight(1f))
        StatCard(label = "Pending", value = "05", icon = Icons.Default.Schedule, modifier = Modifier.weight(1f))
        StatCard(label = "Completed", value = "08", icon = Icons.Default.DoneAll, modifier = Modifier.weight(1f))
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.7f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF6C3EF4), modifier = Modifier.size(20.dp))
            Column {
                Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
                Text(label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun ActionCard(title: String, subtitle: String, buttonText: String, onActionClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(20.dp, RoundedCornerShape(28.dp), ambientColor = Color(0xFF6C3EF4).copy(alpha = 0.2f)),
        shape = RoundedCornerShape(28.dp),
        color = Color.White.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.8f))
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = subtitle,
                    fontSize = 15.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onActionClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(12.dp, RoundedCornerShape(20.dp), ambientColor = Color(0xFF6C3EF4)),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFF6C3EF4), Color(0xFF9F5FFF)))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                buttonText,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun OrganizerHomeScreenPreview() {
    OrganizerHomeScreen(navController = rememberNavController())
}

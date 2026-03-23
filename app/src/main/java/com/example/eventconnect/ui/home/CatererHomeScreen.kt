package com.example.eventconnect.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatererHomeScreen(navController: NavController) {
    val backgroundColor = Brush.verticalGradient(
        colors = listOf(Color.White, Color(0xFFF5F3FF))
    )
    val accentColor = Color(0xFF6C3EF4)
    val titleColor = Color(0xFF1A1C1E)
    val bodyTextColor = Color(0xFF374151)
    val secondaryTextColor = Color(0xFF6B7280)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp)) // Reduced from 24.dp

            // Greeting Section
            Text(
                text = "Welcome back, Caterer",
                fontSize = 16.sp,
                color = secondaryTextColor,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "Caterer Dashboard",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Text(
                text = "Manage your catering services",
                fontSize = 14.sp,
                color = secondaryTextColor,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(16.dp)) // Reduced from 24.dp

            // Quick Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatItem(label = "Orders", value = "24", modifier = Modifier.weight(1f))
                StatItem(label = "Revenue", value = "$1.2k", modifier = Modifier.weight(1f))
                StatItem(label = "Active", value = "5", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp)) // Reduced from 24.dp

            // Dashboard Grid
            DashboardGrid(navController, accentColor, bodyTextColor)
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DashboardGrid(navController: NavController, accentColor: Color, bodyTextColor: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            DashboardGridCard(
                title = "Manage Menu",
                subtitle = "Update dishes",
                icon = Icons.Default.RestaurantMenu,
                accentColor = accentColor,
                textColor = bodyTextColor,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("caterer-menu") }
            )
            DashboardGridCard(
                title = "View Bookings",
                subtitle = "Track orders",
                icon = Icons.Default.EventAvailable,
                accentColor = accentColor,
                textColor = bodyTextColor,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("caterer-bookings") }
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            DashboardGridCard(
                title = "Revenue",
                subtitle = "Check earnings",
                icon = Icons.Default.BarChart,
                accentColor = accentColor,
                textColor = bodyTextColor,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("caterer-revenue") }
            )
            DashboardGridCard(
                title = "History",
                subtitle = "Payment logs",
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                accentColor = accentColor,
                textColor = bodyTextColor,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("caterer-payment-history") }
            )
        }
    }
}

@Composable
fun StatItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6C3EF4)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF6B7280)
        )
    }
}

@Composable
fun DashboardGridCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .aspectRatio(1.1f)
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        color = Color.White,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp) // Reduced internal padding from 16.dp
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp)) // Reduced from 12.dp
            
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

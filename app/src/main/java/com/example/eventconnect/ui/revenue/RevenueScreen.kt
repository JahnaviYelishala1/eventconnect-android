package com.example.eventconnect.ui.revenue

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventconnect.data.network.MonthlyRevenue

@Composable
fun RevenueScreen(
    viewModel: RevenueViewModel = viewModel()
) {
    val revenue by viewModel.revenue.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadRevenue()
    }

    // Color Palette
    val backgroundColor = Color(0xFFF9FAFB)
    val cardBackground = Color.White
    val primaryText = Color(0xFF1A1C1E)
    val secondaryText = Color(0xFF6B7280)
    val purpleAccent = Color(0xFF6366F1)
    val greenStatus = Color(0xFF22C55E)
    val orangeStatus = Color(0xFFF59E0B)

    Scaffold(
        containerColor = backgroundColor,
        bottomBar = { ModernFloatingNavBar() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // TOP SECTION
            Column {
                Text(
                    text = "Revenue Dashboard",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryText,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Overview of your business performance",
                    fontSize = 14.sp,
                    color = secondaryText,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // SUMMARY CARDS
            if (revenue != null) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        SummaryCard(
                            label = "Total Revenue",
                            value = "₹${revenue!!.total_revenue}",
                            icon = Icons.Default.Payments,
                            iconColor = purpleAccent,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryCard(
                            label = "This Month",
                            value = "₹${revenue!!.this_month_revenue}",
                            icon = Icons.Default.CalendarMonth,
                            iconColor = purpleAccent,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        SummaryCard(
                            label = "Paid Bookings",
                            value = revenue!!.total_paid_bookings.toString(),
                            icon = Icons.Default.CheckCircle,
                            iconColor = greenStatus,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryCard(
                            label = "Pending",
                            value = revenue!!.pending_bookings.toString(),
                            icon = Icons.Default.History,
                            iconColor = orangeStatus,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // CHART SECTION
                ChartCard(monthlyData = revenue!!.monthly_breakdown)
            } else if (loading) {
                Box(Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = purpleAccent)
                }
            } else if (error != null) {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(error ?: "Unknown error", color = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(120.dp)) // Spacing for bottom nav
        }
    }
}

@Composable
fun SummaryCard(
    label: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.1f))
            .clip(RoundedCornerShape(20.dp)),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Column {
                Text(label, color = Color(0xFF6B7280), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(value, color = Color(0xFF111827), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun ChartCard(monthlyData: List<MonthlyRevenue>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.1f))
            .clip(RoundedCornerShape(24.dp)),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Monthly Revenue",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF1A1C1E)
            )
            
            Spacer(Modifier.height(32.dp))
            
            ModernBarChart(monthlyData)
            
            Spacer(Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(8.dp).background(Color(0xFF6366F1), CircleShape))
                Spacer(Modifier.width(8.dp))
                Text("Revenue (₹)", fontSize = 12.sp, color = Color(0xFF6B7280))
            }
        }
    }
}

@Composable
fun ModernBarChart(data: List<MonthlyRevenue>) {
    val maxRevenue = (data.maxOfOrNull { it.revenue } ?: 1.0).coerceAtLeast(1.0)
    
    Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        // Grid Lines
        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            repeat(4) {
                HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 1.dp)
            }
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEach { item ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    val heightPercent = (item.revenue / maxRevenue).toFloat()
                    
                    // Value Label above bar
                    Text(
                        text = if (item.revenue >= 1000) "${(item.revenue / 1000).toInt()}k" else item.revenue.toInt().toString(),
                        fontSize = 10.sp,
                        color = Color(0xFF111827),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(heightPercent.coerceAtLeast(0.1f) * 0.75f)
                            .background(
                                color = Color(0xFF6366F1),
                                shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                            )
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = item.month.take(3).uppercase(),
                        fontSize = 10.sp,
                        color = Color(0xFF9CA3AF),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ModernFloatingNavBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .height(64.dp)
                .width(280.dp)
                .shadow(16.dp, RoundedCornerShape(32.dp), spotColor = Color.Black.copy(alpha = 0.15f)),
            color = Color.White,
            shape = RoundedCornerShape(32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavBarItem(Icons.Default.Dashboard, "Home", false)
                NavBarItem(Icons.Default.BarChart, "Analytics", true)
                NavBarItem(Icons.Default.Wallet, "Payments", false)
                NavBarItem(Icons.Default.Person, "Profile", false)
            }
        }
    }
}

@Composable
fun NavBarItem(icon: ImageVector, label: String, isActive: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable { /* Navigate */ }
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = if (isActive) Color(0xFF6366F1) else Color(0xFF9CA3AF),
            modifier = Modifier.size(24.dp)
        )
        if (isActive) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(4.dp)
                    .background(Color(0xFF6366F1), CircleShape)
            )
        }
    }
}

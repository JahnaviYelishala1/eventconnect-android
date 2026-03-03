package com.example.eventconnect.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CatererHomeScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Caterer Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(16.dp))

        DashboardCard(
            title = "Manage Menu",
            icon = Icons.Default.RestaurantMenu
        ) {
            navController.navigate("caterer-menu")
        }

        DashboardCard(
            title = "View Bookings",
            icon = Icons.Default.List
        ) {
            navController.navigate("caterer-bookings")
        }

        DashboardCard(
            title = "Revenue Analytics",
            icon = Icons.Default.BarChart
        ) {
            navController.navigate("caterer-revenue")
        }

        DashboardCard(
            title = "Payment History",
            icon = Icons.Default.ReceiptLong
        ) {
            navController.navigate("caterer-payment-history")
        }
    }
}

@Composable
private fun DashboardCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
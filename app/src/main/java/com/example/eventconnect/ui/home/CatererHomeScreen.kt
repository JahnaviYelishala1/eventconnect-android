package com.example.eventconnect.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CatererHomeScreen(navController: NavController) {

    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {

                // ---------------- HOME ----------------
                NavigationBarItem(
                    selected = selectedIndex == 0,
                    onClick = {
                        selectedIndex = 0
                    },
                    icon = {
                        Icon(
                            Icons.Default.Restaurant,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home") }
                )

                // ---------------- BOOKINGS ----------------
                NavigationBarItem(
                    selected = selectedIndex == 1,
                    onClick = {
                        selectedIndex = 1
                        navController.navigate("caterer-bookings")
                    },
                    icon = {
                        Icon(
                            Icons.Default.List,
                            contentDescription = "Bookings"
                        )
                    },
                    label = { Text("Bookings") }
                )

                NavigationBarItem(
                    selected = selectedIndex == 2,
                    onClick = {
                        selectedIndex = 2
                        navController.navigate("caterer-profile")
                    },
                    icon = {
                        Icon(Icons.Default.AccountCircle, null)
                    },
                    label = { Text("Profile") }
                )

                // ---------------- LOGOUT ----------------
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Logout"
                        )
                    },
                    label = { Text("Logout") }
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
            Text("🍽️ Caterer Home")
        }
    }
}

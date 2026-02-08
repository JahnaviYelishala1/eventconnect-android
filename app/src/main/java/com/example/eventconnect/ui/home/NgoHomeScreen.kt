package com.example.eventconnect.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NgoHomeScreen(navController: NavController) {

    Scaffold(
        bottomBar = {
            NavigationBar {

                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = {
                        Icon(
                            Icons.Default.List,
                            contentDescription = "Available Food"
                        )
                    },
                    label = { Text("Available") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        // later: navigate to claimed food screen
                    },
                    icon = {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Claimed"
                        )
                    },
                    label = { Text("Claimed") }
                )

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
            Text("🤝 NGO Home")
        }
    }
}

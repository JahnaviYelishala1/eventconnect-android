package com.example.eventconnect.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun OrganizerHomeScreen(navController: NavController) {

    val selectedIndex = remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {

                NavigationBarItem(
                    selected = selectedIndex.value == 0,
                    onClick = { selectedIndex.value = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )

                NavigationBarItem(
                    selected = selectedIndex.value == 1,
                    onClick = {
                        selectedIndex.value = 1
                        navController.navigate("create-event")
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Create Event") },
                    label = { Text("Create") }
                )

                NavigationBarItem(
                    selected = selectedIndex.value == 2,
                    onClick = {
                        selectedIndex.value = 2
                        navController.navigate("my-events")
                    },
                    icon = { Icon(Icons.Default.List, contentDescription = "My Events") },
                    label = { Text("My Events") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            // Clear the back stack
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                            // Avoid multiple copies of the same destination when re-selecting the same item
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Logout") },
                    label = { Text("Logout") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (selectedIndex.value) {
                0 -> Text("Home Screen")
                1 -> Text("Create Event Screen")
                2 -> Text("My Events Screen")
            }
        }
    }
}
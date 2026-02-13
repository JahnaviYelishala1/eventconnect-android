package com.example.eventconnect.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerHomeScreen(navController: NavController) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Organizer Dashboard") },
                actions = {
                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("🎉 Organizer Dashboard")
        }
    }
}

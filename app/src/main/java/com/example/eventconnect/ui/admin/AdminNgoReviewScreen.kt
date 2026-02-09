package com.example.eventconnect.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNgoReviewScreen(
    navController: NavController,
    viewModel: AdminNgoViewModel = viewModel()
) {
    val ngos by viewModel.ngos.collectAsState()
    val error by viewModel.error.collectAsState()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.loadNgos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NGO Verification") },
                actions = {
                    IconButton(onClick = { viewModel.loadNgos() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
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
                .padding(padding)
        ) {
            when {
                error != null -> {
                    Text(
                        text = error ?: "Error",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                ngos.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(ngos) { ngo ->
                            Card {
                                Column(modifier = Modifier.padding(16.dp)) {

                                    Text(
                                        ngo.name,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text("Reg No: ${ngo.registration_number}")
                                    Text("NGO Status: ${ngo.status}")

                                    Spacer(modifier = Modifier.height(8.dp))

                                    ngo.documents.forEach { doc ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(doc.document_type)

                                                Text(
                                                    text = "Status: ${doc.status}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = when (doc.status) {
                                                        "APPROVED" -> Color(0xFF2E7D32)
                                                        "REJECTED" -> Color(0xFFC62828)
                                                        else -> Color.Gray
                                                    }
                                                )

                                                Spacer(modifier = Modifier.height(4.dp))

                                                Text(
                                                    text = "View Document",
                                                    color = Color.Blue,
                                                    textDecoration = TextDecoration.Underline,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    modifier = Modifier.clickable {
                                                        uriHandler.openUri(doc.file_url)
                                                    }
                                                )
                                            }

                                            Row {
                                                IconButton(
                                                    enabled = doc.status == "PENDING",
                                                    onClick = {
                                                        viewModel.approveDocument(doc.id)
                                                    }
                                                ) {
                                                    Icon(Icons.Default.Check, "Approve")
                                                }

                                                IconButton(
                                                    enabled = doc.status == "PENDING",
                                                    onClick = {
                                                        viewModel.rejectDocument(doc.id)
                                                    }
                                                ) {
                                                    Icon(Icons.Default.Close, "Reject")
                                                }
                                            }
                                        }
                                        Divider()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

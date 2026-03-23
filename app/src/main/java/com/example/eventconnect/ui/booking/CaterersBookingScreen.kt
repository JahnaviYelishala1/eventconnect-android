package com.example.eventconnect.ui.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatererBookingsScreen(
    navController: NavController,
    viewModel: CatererBookingsViewModel = viewModel()
) {
    val bookings by viewModel.bookings.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    val backgroundColor = Brush.verticalGradient(
        colors = listOf(Color.White, Color(0xFFF5F3FF))
    )
    val accentColor = Color(0xFF6C3EF4)
    val titleColor = Color(0xFF1A1C1E)
    val secondaryTextColor = Color(0xFF6B7280)

    LaunchedEffect(Unit) {
        viewModel.loadBookings()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Booking Requests",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
                
                Text(
                    text = "Manage your event bookings",
                    fontSize = 14.sp,
                    color = secondaryTextColor,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            when {
                loading && bookings.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = accentColor)
                    }
                }

                error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = error ?: "An unknown error occurred",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                bookings.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No booking requests yet",
                            color = secondaryTextColor,
                            fontSize = 16.sp
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(bookings) { booking ->
                            ExpandableBookingCard(
                                booking = booking,
                                showActions = true,
                                onAccept = {
                                    viewModel.updateStatus(booking.id, "accepted")
                                },
                                onReject = {
                                    viewModel.updateStatus(booking.id, "rejected")
                                },
                                onChat = {
                                    navController.navigate("chat/${booking.id}")
                                },
                                onTrackPreparation = {
                                    navController.navigate("caterer-preparation/${booking.id}")
                                },
                                onPredictFood = {
                                    navController.navigate("food_prediction/${booking.id}")
                                },
                                onAskAi = { 
                                    navController.navigate("ai-chat/${booking.id}") 
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

package com.example.eventconnect.ui.booking

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eventconnect.data.network.PaymentResponse
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerBookingsScreen(
    navController: NavController,
    viewModel: OrganizerBookingsViewModel = viewModel()
) {
    val bookings by viewModel.bookings.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    val paymentMap = remember { mutableStateMapOf<Int, PaymentResponse>() }
    var showRefundDialog by remember { mutableStateOf(false) }
    var selectedBookingId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(bookings) {
        bookings.forEach { booking ->
            if (booking.status == "paid" && !paymentMap.containsKey(booking.id)) {
                viewModel.getPaymentDetails(booking.id) { payment ->
                    payment?.let { paymentMap[booking.id] = it }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadBookings()
    }

    if (showRefundDialog && selectedBookingId != null) {
        AlertDialog(
            onDismissRequest = {
                showRefundDialog = false
                selectedBookingId = null
            },
            title = { Text("Confirm Refund", color = Color(0xFF1A1C1E), fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to refund this payment? This action cannot be undone.", color = Color(0xFF4A4A4A)) },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30)),
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        viewModel.refundBooking(selectedBookingId!!)
                        showRefundDialog = false
                        selectedBookingId = null
                    }
                ) { Text("Yes, Refund", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRefundDialog = false
                    selectedBookingId = null
                }) { Text("Cancel", color = Color(0xFF6B6B6B)) }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF9F7FF), // Very light lavender neutral background
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Booking Requests",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1A1C1E),
                        letterSpacing = (-0.5).sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF1A1C1E))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color(0xFF1A1C1E)
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF6C3EF4)
                )
                error != null -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(error ?: "Unknown error", color = Color(0xFFFF3B30), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadBookings() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C3EF4))) {
                        Text("Retry")
                    }
                }
                bookings.isEmpty() -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.EventNote,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFF6B6B6B).copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "No booking requests yet.",
                        fontSize = 16.sp,
                        color = Color(0xFF6B6B6B),
                        fontWeight = FontWeight.Medium
                    )
                }
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp, start = 20.dp, end = 20.dp, top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(bookings) { booking ->
                        ExpandableBookingCard(
                            booking = booking,
                            showActions = false,
                            paymentDetails = paymentMap[booking.id],
                            onCancel = { viewModel.cancelBooking(booking.id) },
                            onPay = {
                                viewModel.createPaymentSession(booking.id) { url ->
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                }
                            },
                            onRefund = {
                                selectedBookingId = booking.id
                                showRefundDialog = true
                            },
                            onDownloadInvoice = {
                                viewModel.downloadInvoice(booking.id) { bytes ->
                                    val file = File(context.getExternalFilesDir(null), "invoice_${booking.id}.pdf")
                                    file.writeBytes(bytes)
                                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(uri, "application/pdf")
                                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    }
                                    context.startActivity(intent)
                                }
                            },
                            onChat = { navController.navigate("chat/${booking.id}") },
                            onTrackPreparation = { navController.navigate("preparation/${booking.id}") },
                            onAskAi = { navController.navigate("ai-chat/${booking.id}") }
                        )
                    }
                }
            }
        }
    }
}

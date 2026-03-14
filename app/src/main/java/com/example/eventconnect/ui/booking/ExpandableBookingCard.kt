package com.example.eventconnect.ui.booking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventconnect.data.network.BookingResponse
import com.example.eventconnect.data.network.PaymentResponse
import java.util.*

@Composable
fun ExpandableBookingCard(
    booking: BookingResponse,
    showActions: Boolean,
    onAccept: (() -> Unit)? = null,
    onReject: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onPay: (() -> Unit)? = null,
    paymentDetails: PaymentResponse? = null,
    onRefund: (() -> Unit)? = null,
    onDownloadInvoice: (() -> Unit)? = null,
    onChat: (() -> Unit)? = null,
    onTrackPreparation: (() -> Unit)? = null,
    onPredictFood: (() -> Unit)? = null
) {

    var expanded by remember { mutableStateOf(false) }
    val status = booking.status.lowercase(Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {

                Column(modifier = Modifier.weight(1f)) {

                    Text(
                        text = booking.event_name ?: "Event",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Caterer: ${booking.caterer_name ?: "N/A"}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                val statusColor = when (status) {
                    "pending" -> Color(0xFFFF9500)
                    "accepted" -> Color(0xFF007AFF)
                    "paid" -> Color(0xFF34C759)
                    "rejected", "cancelled" -> Color(0xFFFF3B30)
                    else -> Color.Gray
                }

                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {

                    Text(
                        text = status.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider()

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                BookingInfoBox("Guests", (booking.attendees ?: 0).toString())
                BookingInfoBox("Price", "₹${booking.total_price}")
                BookingInfoBox("Booking ID", "#${booking.id}")
            }

            AnimatedVisibility(visible = expanded) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF2F2F7), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                        .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(8.dp))
                ) {

                    Text(
                        "Order Summary",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    booking.items.forEach { item ->

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Text("${item.item_name} x${item.quantity}")

                            Text("₹${item.price}", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                // ---------------- ORGANIZER ACTIONS ----------------
                if (!showActions) {

                    if (status == "accepted") {

                        BookingActionButton(
                            text = "Pay Now",
                            containerColor = Color.Black,
                            contentColor = Color.White
                        ) { onPay?.invoke() }
                    }

                    if (status == "paid" && paymentDetails != null) {

                        Text(
                            "Payment Confirmed ✅",
                            color = Color(0xFF34C759),
                            fontWeight = FontWeight.Bold
                        )

                        BookingActionButton("Download Invoice", isOutlined = true) {
                            onDownloadInvoice?.invoke()
                        }
                    }

                    if (status != "cancelled" && status != "rejected") {

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                            Box(Modifier.weight(1f)) {
                                BookingActionButton("Chat", isOutlined = true) {
                                    onChat?.invoke()
                                }
                            }

                            Box(Modifier.weight(1f)) {
                                BookingActionButton(
                                    "Track",
                                    containerColor = Color.Black,
                                    contentColor = Color.White
                                ) {
                                    onTrackPreparation?.invoke()
                                }
                            }
                        }
                    }
                }

                // ---------------- CATERER ACTIONS ----------------
                else {

                    if (status == "pending") {

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                            Box(Modifier.weight(1f)) {
                                BookingActionButton(
                                    "Accept",
                                    containerColor = Color(0xFF34C759),
                                    contentColor = Color.White
                                ) { onAccept?.invoke() }
                            }

                            Box(Modifier.weight(1f)) {
                                BookingActionButton(
                                    "Reject",
                                    isOutlined = true,
                                    contentColor = Color.Red
                                ) { onReject?.invoke() }
                            }
                        }
                    }

                    if (status != "cancelled" && status != "rejected") {

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                                Box(Modifier.weight(1f)) {
                                    BookingActionButton("Chat", isOutlined = true) {
                                        onChat?.invoke()
                                    }
                                }

                                Box(Modifier.weight(1f)) {
                                    BookingActionButton(
                                        "Predict Food",
                                        containerColor = Color.Black,
                                        contentColor = Color.White
                                    ) {
                                        onPredictFood?.invoke()
                                    }
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                                Box(Modifier.weight(1f)) {
                                    BookingActionButton(
                                        "Update Preparation",
                                        containerColor = Color(0xFF007AFF),
                                        contentColor = Color.White
                                    ) {
                                        onTrackPreparation?.invoke()
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

@Composable
fun BookingInfoBox(label: String, value: String) {

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(label, fontSize = 12.sp, color = Color.Gray)

        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BookingActionButton(
    text: String,
    containerColor: Color = Color.Transparent,
    contentColor: Color = Color.Black,
    isOutlined: Boolean = false,
    onClick: () -> Unit
) {

    if (isOutlined) {

        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Text(text)
        }

    } else {

        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor
            )
        ) {
            Text(text)
        }
    }
}
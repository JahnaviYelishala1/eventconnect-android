package com.example.eventconnect.ui.booking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    onTrackPreparation: (() -> Unit)? = null   // ✅ NEW
) {

    var expanded by remember { mutableStateOf(false) }
    val status = booking.status.lowercase(Locale.getDefault())

    val statusColor = when (status) {
        "pending" -> MaterialTheme.colorScheme.secondary
        "accepted" -> MaterialTheme.colorScheme.primary
        "paid" -> MaterialTheme.colorScheme.tertiary
        "refunded" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.secondary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = booking.event_name ?: "Event",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text("Caterer: ${booking.caterer_name ?: "N/A"}")
            Text("Guests: ${booking.attendees ?: 0}")
            Text("Total: ₹${booking.total_price}")

            Spacer(Modifier.height(6.dp))

            AssistChip(
                onClick = {},
                label = { Text(status.uppercase(Locale.getDefault())) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = statusColor.copy(alpha = 0.2f)
                )
            )

            Spacer(Modifier.height(12.dp))

            // ================================
            // ORGANIZER SIDE
            // ================================
            if (!showActions) {

                if (status == "accepted") {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onPay?.invoke() }
                    ) {
                        Text("Pay Now")
                    }
                    Spacer(Modifier.height(8.dp))
                }

                if (status == "paid" && paymentDetails != null) {

                    Text(
                        "Payment Completed ✅",
                        color = MaterialTheme.colorScheme.tertiary
                    )

                    Text("₹${paymentDetails.amount} paid")
                    Text("${paymentDetails.card_brand?.uppercase()} •••• ${paymentDetails.card_last4}")

                    Spacer(Modifier.height(8.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onDownloadInvoice?.invoke() }
                    ) {
                        Text("Download Invoice")
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        onClick = { onRefund?.invoke() }
                    ) {
                        Text("Request Refund")
                    }

                    Spacer(Modifier.height(8.dp))
                }

                if (status == "refunded") {
                    Text(
                        "Refunded ❌",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                if (status != "cancelled" && status != "rejected") {

                    Spacer(Modifier.height(8.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onChat?.invoke() }
                    ) {
                        Text("Chat with Caterer")
                    }

                    Spacer(Modifier.height(8.dp))

                    // ✅ NEW BUTTON (Organizer)
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onTrackPreparation?.invoke() }
                    ) {
                        Text("Track Preparation")
                    }
                }
            }

            // ================================
            // CATERER SIDE
            // ================================
            if (showActions) {

                when (status) {

                    "pending" -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(onClick = { onAccept?.invoke() }) {
                                Text("Accept")
                            }

                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                ),
                                onClick = { onReject?.invoke() }
                            ) {
                                Text("Reject")
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    "accepted" -> {
                        Text(
                            "Awaiting Payment from Organizer",
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    "paid" -> {
                        Text(
                            "Payment Received ✅",
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }

                if (status != "cancelled" && status != "rejected") {

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onChat?.invoke() }
                    ) {
                        Text("Chat with Organizer")
                    }

                    Spacer(Modifier.height(8.dp))

                    // ✅ NEW BUTTON (Caterer)
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onTrackPreparation?.invoke() }
                    ) {
                        Text("Update Preparation")
                    }
                }
            }

            if (expanded) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))

                booking.items.forEach { item ->
                    Text("${item.item_name} x${item.quantity} - ₹${item.price}")
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}
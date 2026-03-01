package com.example.eventconnect.ui.booking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.eventconnect.data.network.BookingResponse
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExpandableBookingCard(
    booking: BookingResponse,
    showActions: Boolean,
    onAccept: (() -> Unit)? = null,
    onReject: (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    val statusColor = when (booking.status) {
        "accepted" -> MaterialTheme.colorScheme.primary
        "rejected" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.secondary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { expanded = !expanded }
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(
                booking.event_name ?: "Event",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(4.dp))

            Text("Caterer: ${booking.caterer_name ?: "N/A"}")
            Text("Guests: ${booking.attendees ?: 0}")

            val formattedDate = booking.booking_date?.let {
                try {
                    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    val date = parser.parse(it)
                    if (date != null) formatter.format(date) else it
                } catch (e: Exception) { it }
            } ?: "N/A"

            Text("Date: $formattedDate")
            Text("Total: ₹${booking.total_price}")

            Spacer(Modifier.height(6.dp))

            AssistChip(
                onClick = {},
                label = { Text(booking.status.uppercase()) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = statusColor.copy(alpha = 0.2f)
                )
            )

            if (expanded) {

                Spacer(Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))

                booking.items.forEach { item ->
                    Text(
                        "${item.item_name}  x${item.quantity}  - ₹${item.price}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(4.dp))
                }

                if (showActions && booking.status == "pending") {
                    Spacer(Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

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
                }
            }
        }
    }
}

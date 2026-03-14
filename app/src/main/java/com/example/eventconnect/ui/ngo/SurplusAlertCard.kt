package com.example.eventconnect.ui.ngo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SurplusAlertCard(
    eventName: String,
    food: String,
    distance: Double,
    onAccept: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text("Event: $eventName")

            Text("Food: $food")

            Text("Distance: ${distance} km")

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onAccept
            ) {
                Text("Accept Pickup")
            }
        }
    }
}
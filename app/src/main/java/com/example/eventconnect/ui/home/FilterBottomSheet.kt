package com.example.eventconnect.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    onApply: (Double?, Double?, Double?) -> Unit,
    onDismiss: () -> Unit
) {

    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    var minRating by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            Text(
                "Filters",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = minPrice,
                onValueChange = { minPrice = it },
                label = { Text("Min Price") }
            )

            OutlinedTextField(
                value = maxPrice,
                onValueChange = { maxPrice = it },
                label = { Text("Max Price") }
            )

            OutlinedTextField(
                value = minRating,
                onValueChange = { minRating = it },
                label = { Text("Minimum Rating") }
            )

            Button(
                onClick = {
                    onApply(
                        minPrice.toDoubleOrNull(),
                        maxPrice.toDoubleOrNull(),
                        minRating.toDoubleOrNull()
                    )
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Apply Filters")
            }
        }
    }
}

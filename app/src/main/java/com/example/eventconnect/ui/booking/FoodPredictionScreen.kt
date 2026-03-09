package com.example.eventconnect.ui.booking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun FoodPredictionScreen(
    bookingId: Int,
    viewModel: FoodPredictionViewModel = viewModel()
) {

    val predictions by viewModel.predictions.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.predictFood(bookingId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Food Preparation Plan",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn {

            items(predictions.toList()) { item ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(item.first)

                    Text("${item.second} kg")
                }

                Divider()
            }
        }
    }
}
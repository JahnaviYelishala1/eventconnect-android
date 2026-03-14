package com.example.eventconnect.ui.surplus

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun WaitingForNGOScreen(
    viewModel: SurplusViewModel = viewModel()
) {

    val ngo by viewModel.acceptedNgo.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        if (ngo == null) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                CircularProgressIndicator()

                Spacer(modifier = Modifier.height(20.dp))

                Text("Waiting for NGO to accept food donation...")
            }

        } else {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text(
                    text = "NGO Accepted!",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("NGO: ${ngo!!.ngo_name}")

                Text("Contact: ${ngo!!.phone}")
            }
        }
    }
}
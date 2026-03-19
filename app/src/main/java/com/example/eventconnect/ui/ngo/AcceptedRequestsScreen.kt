package com.example.eventconnect.ui.ngo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventconnect.data.network.AcceptedRequestResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcceptedRequestsScreen(
    viewModel: AcceptedRequestsViewModel = viewModel()
) {

    val requests by viewModel.requests.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadRequests()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Current Accepted Requests")
                }
            )
        }
    ) { padding ->

        if (requests.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("No accepted requests yet")
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                items(requests) { request ->

                    AcceptedRequestCard(request)

                }

            }

        }

    }

}

@Composable
fun AcceptedRequestCard(
    request: AcceptedRequestResponse
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = request.event_name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = request.food_description
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Pickup Location:"
            )

            Text(
                text = "${request.latitude}, ${request.longitude}"
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Requested at: ${request.created_at}"
            )

        }

    }

}
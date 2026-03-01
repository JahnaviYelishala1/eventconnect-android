package com.example.eventconnect.ui.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.Image

@Composable
fun CatererMenuManagementScreen(
    viewModel: CatererMenuManagementViewModel = viewModel()
) {

    val menu by viewModel.menu.collectAsState()
    val loading by viewModel.loading.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadMenu()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Menu Item")
        }

        Spacer(Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator()
        } else {
            menu.forEach { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {

                        item.image_url?.let {
                            Image(
                                painter = rememberAsyncImagePainter(it),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        Text(item.item_name)
                        Text("₹${item.price}")
                        Text(item.category ?: "")

                        Spacer(Modifier.height(8.dp))

                        Button(onClick = {
                            viewModel.deleteMenu(item.id)
                        }) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddMenuDialog(
            onDismiss = { showDialog = false },
            onSave = { request, imageUri ->
                viewModel.addMenu(request, imageUri)
                showDialog = false
            }
        )
    }
}
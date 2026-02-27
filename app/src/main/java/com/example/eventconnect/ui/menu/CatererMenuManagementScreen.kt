package com.example.eventconnect.ui.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventconnect.data.network.MenuResponse

@OptIn(ExperimentalMaterial3Api::class)
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
                MenuCard(
                    item = item,
                    onDelete = { viewModel.deleteMenu(item.id) }
                )
            }
        }
    }

    if (showDialog) {
        AddMenuDialog(
            onDismiss = { showDialog = false },
            onSave = {
                viewModel.addMenu(it)
                showDialog = false
            }
        )
    }
}

@Composable
private fun MenuCard(
    item: MenuResponse,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(
                text = item.item_name,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(4.dp))

            Text("₹${item.price}")

            Spacer(Modifier.height(8.dp))

            Button(onClick = onDelete) {
                Text("Delete")
            }
        }
    }
}


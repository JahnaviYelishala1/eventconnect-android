package com.example.eventconnect.ui.surplus

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendSurplusScreen(
    navController: NavController,
    eventId: Int,
    latitude: Double,
    longitude: Double,
    viewModel: SurplusViewModel = viewModel()
) {

    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val success by viewModel.success.collectAsState()

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            // For demo we just ignore upload and show placeholder
        }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Donate Surplus Food") }) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Food Description") },
                placeholder = {
                    Text("Example: 50 plates Veg Biryani, Dal, Naan")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Button(
                    onClick = { galleryLauncher.launch("image/*") }
                ) {
                    Text("Pick from Gallery")
                }

                Button(
                    onClick = { cameraLauncher.launch(null) }
                ) {
                    Text("Use Camera")
                }

            }

            selectedImageUri?.let {

                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Selected Food Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                onClick = {

                    viewModel.sendAlert(
                        eventId,
                        description,
                        "",   // placeholder image_url
                        latitude,
                        longitude
                    )

                }
            ) {
                Text("Send Alert to NGOs")
            }

            if (success) {

                LaunchedEffect(success) {
                    viewModel.requestId?.let { reqId ->
                        navController.navigate("waiting-ngo/$reqId")
                    }
                }

                Text(
                    "Alert sent to nearby NGOs!",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
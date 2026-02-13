package com.example.eventconnect.ui.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.eventconnect.data.network.OrganizerProfileRequest
import com.example.eventconnect.data.network.OrganizerProfileResponse

@Composable
fun OrganizerProfileForm(
    existing: OrganizerProfileResponse? = null,
    onSubmit: (OrganizerProfileRequest, Uri?) -> Unit
) {

    var name by remember { mutableStateOf(existing?.full_name ?: "") }
    var company by remember { mutableStateOf(existing?.organization_name ?: "") }
    var phone by remember { mutableStateOf(existing?.phone ?: "") }
    var city by remember { mutableStateOf(existing?.city ?: "") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf(existing?.profile_image_url) }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Text(
            text = "Organizer Profile",
            style = MaterialTheme.typography.headlineSmall
        )

        // 🔥 IMPROVED PROFILE IMAGE SECTION
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { imagePicker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {

                val painter = rememberAsyncImagePainter(
                    model = selectedImageUri ?: imageUrl ?: ""
                )

                if (selectedImageUri != null || imageUrl != null) {
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("Tap to Upload", color = Color.Gray)
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tap image to select from gallery",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = company,
            onValueChange = { company = it },
            label = { Text("Company Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("City") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                onSubmit(
                    OrganizerProfileRequest(
                        full_name = name,
                        organization_name = company,
                        phone = phone,
                        city = city,
                        profile_image_url = imageUrl
                    ),
                    selectedImageUri
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Profile")
        }
    }
}

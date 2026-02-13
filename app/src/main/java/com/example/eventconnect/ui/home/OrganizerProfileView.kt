package com.example.eventconnect.ui.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.eventconnect.data.network.OrganizerProfileResponse

@Composable
fun OrganizerProfileView(
    profile: OrganizerProfileResponse,
    onEdit: () -> Unit
) {

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Image(
            painter = rememberAsyncImagePainter(
                model = profile.profile_image_url ?: "https://via.placeholder.com/300"
            ),
            contentDescription = null,
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Text("Name: ${profile.full_name}")
        Text("Company: ${profile.organization_name}")
        Text("Phone: ${profile.phone}")
        Text("City: ${profile.city}")

        Button(
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Edit Profile")
        }
    }
}

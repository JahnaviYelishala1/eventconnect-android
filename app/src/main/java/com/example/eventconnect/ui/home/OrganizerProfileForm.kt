package com.example.eventconnect.ui.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.eventconnect.data.network.OrganizerProfileRequest
import com.example.eventconnect.data.network.OrganizerProfileResponse

@OptIn(ExperimentalMaterial3Api::class)
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

    // Modern Color Palette
    val bgColor = Color(0xFFF8FAFC)
    val cardBg = Color(0xFFFFFFFF)
    val primaryPurple = Color(0xFF6C3EF4)
    val gradientStart = Color(0xFF7C3AED)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)
    val fieldBg = Color(0xFFF1F5F9)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .verticalScroll(rememberScrollState())
    ) {
        // Profile Image Section
        Spacer(modifier = Modifier.height(32.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .shadow(elevation = 8.dp, shape = CircleShape, spotColor = Color(0x33000000))
                    .clip(CircleShape)
                    .background(Color.White)
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
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = textSecondary.copy(alpha = 0.5f)
                    )
                }

                // Camera Overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .size(28.dp)
                        .shadow(elevation = 4.dp, shape = CircleShape)
                        .background(primaryPurple, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Upload Profile Photo",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = textPrimary,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        // Form Container
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .shadow(elevation = 10.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0x1A000000)),
            shape = RoundedCornerShape(20.dp),
            color = cardBg
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileInputField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Full Name",
                    icon = Icons.Default.Person,
                    fieldBg = fieldBg,
                    textPrimary = textPrimary,
                    primaryPurple = primaryPurple
                )

                ProfileInputField(
                    value = company,
                    onValueChange = { company = it },
                    label = "Company Name",
                    icon = Icons.Default.Business,
                    fieldBg = fieldBg,
                    textPrimary = textPrimary,
                    primaryPurple = primaryPurple
                )

                ProfileInputField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "Phone",
                    icon = Icons.Default.Phone,
                    fieldBg = fieldBg,
                    textPrimary = textPrimary,
                    primaryPurple = primaryPurple
                )

                ProfileInputField(
                    value = city,
                    onValueChange = { city = it },
                    label = "City",
                    icon = Icons.Default.LocationOn,
                    fieldBg = fieldBg,
                    textPrimary = textPrimary,
                    primaryPurple = primaryPurple
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Save Button
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp), spotColor = primaryPurple.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(listOf(gradientStart, primaryPurple))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Save Profile",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    fieldBg: Color,
    textPrimary: Color,
    primaryPurple: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = textPrimary
            )
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            leadingIcon = {
                Icon(icon, contentDescription = null, tint = primaryPurple)
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = fieldBg,
                unfocusedContainerColor = fieldBg,
                disabledContainerColor = fieldBg,
                focusedIndicatorColor = primaryPurple,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = textPrimary,
                unfocusedTextColor = textPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
    }
}

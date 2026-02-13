package com.example.eventconnect.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.eventconnect.data.network.OrganizerProfileResponse
import com.google.firebase.auth.FirebaseAuth

@Composable
fun OrganizerProfileScreen(
    navController: NavController,
    viewModel: OrganizerProfileViewModel = viewModel()
) {

    val profile by viewModel.profile.collectAsState()
    val loading by viewModel.loading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    if (loading) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    when {
        profile == null -> {
            // Assuming OrganizerProfileForm is defined elsewhere
            OrganizerProfileForm { request, uri ->
                viewModel.createProfile(request, uri)
            }
        }

        !viewModel.isEditing.value -> {
            OrganizerProfileContent(
                profile = profile!!,
                onEdit = { viewModel.isEditing.value = true },
                navController = navController
            )
        }

        else -> {
            // Assuming OrganizerProfileForm is defined elsewhere
            OrganizerProfileForm(
                existing = profile,
                onSubmit = { request, uri ->
                    viewModel.updateProfile(request, uri)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerProfileContent(
    profile: OrganizerProfileResponse,
    onEdit: () -> Unit,
    navController: NavController
) {
    val email = FirebaseAuth.getInstance().currentUser?.email
    val backgroundColor = Color(0xFFFDF6E3) // Light cream
    val buttonColor = Color(0xFF9EAD8F) // Grayish green
    val darkTextColor = Color.Black

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("EventConnect", fontWeight = FontWeight.Bold, color = darkTextColor)
                },
                actions = {
                    IconButton(onClick = { /* TODO: Notification screen */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = darkTextColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = profile.profile_image_url,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.Gray), // Placeholder background
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = profile.full_name, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = darkTextColor)
                    email?.let { Text(text = it, fontSize = 16.sp, color = Color.Gray) }
                    Text(text = profile.phone, fontSize = 16.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onEdit,
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                modifier = Modifier.align(Alignment.Start)
            ) {
                Text("Edit Profile", color = darkTextColor)
            }

            Spacer(modifier = Modifier.height(32.dp))

            val menuItems = listOf("Order History", "Favourites", "Settings", "Payment Methods", "Help & Support")
            Column(modifier = Modifier.fillMaxWidth()) {
                menuItems.forEach { item ->
                    Column(modifier = Modifier.clickable { /* TODO */ }) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item,
                                fontSize = 18.sp,
                                color = darkTextColor
                            )
                            Spacer(Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = null, // Decorative icon
                                tint = darkTextColor
                            )
                        }
                        Divider(color = Color.LightGray)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
            ) {
                Text("Logout", color = Color.White, fontSize = 18.sp, modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

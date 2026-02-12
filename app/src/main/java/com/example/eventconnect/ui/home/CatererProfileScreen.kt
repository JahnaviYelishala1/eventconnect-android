package com.example.eventconnect.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.eventconnect.R
import com.example.eventconnect.data.network.CatererProfileResponse
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatererProfileScreen(
    navController: NavController,
    viewModel: CatererProfileViewModel = viewModel()
) {

    val profile by viewModel.profile.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val firebaseUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Scaffold(
        containerColor = Color(0xFFF5EFE7),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.eventeats_logo),
                        contentDescription = "Logo",
                        modifier = Modifier.height(32.dp)
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO: Notification screen */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color(0xFF7A9B8E))
                    }
                }
            )
        }
    ) { padding ->

        if (loading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Box(
            modifier = Modifier.padding(padding)
        ) {

            when {
                profile == null -> {
                    // No profile → Show form
                    CatererProfileForm(
                        onSubmit = { request, uri ->
                            viewModel.createProfile(request, uri)
                        }
                    )
                }

                !viewModel.isEditing.value -> {
                    // Profile exists → Show view
                    CatererProfileView(
                        profile = profile!!,
                        email = firebaseUser?.email,
                        phone = firebaseUser?.phoneNumber,
                        onEdit = {
                            viewModel.isEditing.value = true
                        },
                        onLogout = {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                else -> {
                    // Editing mode
                    CatererProfileForm(
                        existing = profile,
                        onSubmit = { request, uri ->
                            viewModel.updateProfile(request, uri)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CatererProfileView(
    profile: CatererProfileResponse,
    email: String?,
    phone: String?,
    onEdit: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5EFE7))
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        // --- USER INFO ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = profile.image_url ?: "https://via.placeholder.com/150"
                ),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(20.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    profile.business_name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.Black
                )
                email?.let {
                    Text(it, fontSize = 14.sp, color = Color.DarkGray)
                }
                // Use phone from firebase if available, otherwise the placeholder from image
                Text(phone ?: "923232334", fontSize = 14.sp, color = Color.DarkGray)
                
                Spacer(Modifier.height(4.dp))

                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDCDAD4)),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 6.dp),
                ) {
                    Text("Edit Profile", color = Color(0xFF2C3E3F), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
        
        // --- MENU ITEMS ---
        ProfileMenuItem("Order History") { /* TODO */ }
        ProfileMenuItem("Favourites") { /* TODO */ }
        ProfileMenuItem("Settings") { /* TODO */ }
        ProfileMenuItem("Payment Methods") { /* TODO */ }
        ProfileMenuItem("Help & Support") { /* TODO */ }

        // Spacer to push logout to the bottom if content is short
        Spacer(modifier = Modifier.weight(1f))

        // --- LOGOUT ---
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A9B8E)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Logout", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ProfileMenuItem(title: String, onClick: () -> Unit) {
    Column(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                color = Color(0xFF3A3A3A) // A dark, but not pure black color for text
            )
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.Gray
            )
        }
        Divider(color = Color.Black.copy(alpha = 0.08f))
    }
}

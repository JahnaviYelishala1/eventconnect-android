package com.example.eventconnect.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.eventconnect.data.network.OrganizerProfileResponse
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerProfileScreen(
    navController: NavController,
    viewModel: OrganizerProfileViewModel = viewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val isEditing by viewModel.isEditing

    // Modern Color Palette
    val bgColor = Color(0xFFF8FAFC)
    val textPrimary = Color(0xFF111827)
    val primaryPurple = Color(0xFF6C3EF4)

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            Surface(shadowElevation = 2.dp) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                    title = {
                        Text(
                            "Organizer Profile",
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = primaryPurple
                )
            } else {
                when {
                    profile == null -> {
                        OrganizerProfileForm { request, uri ->
                            viewModel.createProfile(request, uri)
                        }
                    }

                    isEditing -> {
                        OrganizerProfileForm(
                            existing = profile,
                            onSubmit = { request, uri ->
                                viewModel.updateProfile(request, uri)
                            }
                        )
                    }

                    else -> {
                        OrganizerProfileContent(
                            profile = profile!!,
                            onEdit = { viewModel.isEditing.value = true },
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrganizerProfileContent(
    profile: OrganizerProfileResponse,
    onEdit: () -> Unit,
    navController: NavController
) {
    val email = FirebaseAuth.getInstance().currentUser?.email
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)
    val primaryPurple = Color(0xFF6C3EF4)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Profile Image Section
        Box(
            modifier = Modifier
                .size(100.dp)
                .shadow(elevation = 8.dp, shape = CircleShape, spotColor = Color(0x33000000))
                .clip(CircleShape)
                .background(Color.White)
        ) {
            AsyncImage(
                model = profile.profile_image_url,
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = profile.full_name,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = textPrimary
        )

        email?.let {
            Text(
                text = it,
                fontSize = 16.sp,
                color = textSecondary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Edit Profile Button
        Button(
            onClick = onEdit,
            colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Edit Profile", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Menu Items
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0x1A000000)),
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                ProfileMenuItem(
                    icon = Icons.Default.History,
                    label = "Order History",
                    onClick = { /* TODO */ },
                    textPrimary = textPrimary,
                    primaryPurple = primaryPurple
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                ProfileMenuItem(
                    icon = Icons.Default.FavoriteBorder,
                    label = "Favourites",
                    onClick = { /* TODO */ },
                    textPrimary = textPrimary,
                    primaryPurple = primaryPurple
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    onClick = { /* TODO */ },
                    textPrimary = textPrimary,
                    primaryPurple = primaryPurple
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                ProfileMenuItem(
                    icon = Icons.Default.Payment,
                    label = "Payment Methods",
                    onClick = { /* TODO */ },
                    textPrimary = textPrimary,
                    primaryPurple = primaryPurple
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                ProfileMenuItem(
                    icon = Icons.Default.HelpOutline,
                    label = "Help & Support",
                    onClick = { /* TODO */ },
                    textPrimary = textPrimary,
                    primaryPurple = primaryPurple
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Logout Button
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color(0xFFEF4444))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout", color = Color(0xFFEF4444), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    textPrimary: Color,
    primaryPurple: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp),
            color = primaryPurple.copy(alpha = 0.1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = primaryPurple,
                modifier = Modifier.padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = textPrimary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFFCBD5E1)
        )
    }
}

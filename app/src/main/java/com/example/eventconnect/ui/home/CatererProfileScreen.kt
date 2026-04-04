package com.example.eventconnect.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.platform.LocalContext
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

// Modern Design Color Palette
private val BackgroundColor = Color(0xFFF8FAFC)
private val CardColor = Color(0xFFFFFFFF)
private val PrimaryPurple = Color(0xFF6C63FF)
private val SecondaryAccent = Color(0xFF10B981)
private val TextPrimary = Color(0xFF111827)
private val TextSecondary = Color(0xFF6B7280)
private val BorderColor = Color(0xFFE5E7EB)
private val UnselectedColor = Color(0xFF9CA3AF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatererProfileScreen(
    navController: NavController,
    viewModel: CatererProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val profile by viewModel.profile.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val firebaseUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            Surface(shadowElevation = 4.dp) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = CardColor),
                    title = {
                        Image(
                            painter = painterResource(id = R.drawable.eventeats_logo),
                            contentDescription = "Logo",
                            modifier = Modifier.height(32.dp)
                        )
                    },
                    actions = {
                        IconButton(onClick = { /* TODO: Notification screen */ }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = PrimaryPurple)
                        }
                    }
                )
            }
        }
    ) { padding ->

        if (loading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryPurple)
            }
            return@Scaffold
        }

        Box(
            modifier = Modifier.padding(padding)
        ) {

            when {
                profile == null -> {
                    CatererProfileForm(
                        onSubmit = { request, uri ->
                            viewModel.createProfile(context, request, uri)
                        }
                    )
                }

                !viewModel.isEditing.value -> {
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
                    CatererProfileForm(
                        existing = profile,
                        onSubmit = { request, uri ->
                            viewModel.updateProfile(context, request, uri)
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
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- PROFILE HEADER CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardColor),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = profile.image_url ?: "https://via.placeholder.com/150"
                    ),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(2.dp, BorderColor, CircleShape)
                        .shadow(4.dp, CircleShape),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = profile.business_name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextPrimary
                )
                
                Text(
                    text = email ?: "caterer@example.com",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                
                Text(
                    text = phone ?: "Not provided",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.height(44.dp).shadow(2.dp, RoundedCornerShape(24.dp))
                ) {
                    Text("Edit Profile", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- MENU OPTIONS SECTION ---
        Text(
            text = "Menu Options",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            ),
            modifier = Modifier.padding(top = 8.dp, start = 4.dp)
        )

        ProfileMenuCard("Order History", Icons.Default.History) { /* TODO */ }
        ProfileMenuCard("Favourites", Icons.Default.Favorite) { /* TODO */ }
        ProfileMenuCard("Settings", Icons.Default.Settings) { /* TODO */ }
        ProfileMenuCard("Payment Methods", Icons.Default.CreditCard) { /* TODO */ }
        ProfileMenuCard("Help & Support", Icons.Default.Help) { /* TODO */ }

        Spacer(modifier = Modifier.height(8.dp))

        // --- LOGOUT BUTTON ---
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .shadow(2.dp, RoundedCornerShape(12.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Logout", color = Color(0xFFEF4444), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ProfileMenuCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryPurple,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium)
            }
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = UnselectedColor
            )
        }
    }
}

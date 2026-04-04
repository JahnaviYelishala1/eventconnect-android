package com.example.eventconnect.ui.profile

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.eventconnect.R
import com.example.eventconnect.data.network.NgoProfile
import com.example.eventconnect.data.network.RetrofitClient
import com.example.eventconnect.ui.auth.getFirebaseIdToken
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NgoProfileScreen(navController: NavController) {

    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(true) }
    var ngo by remember { mutableStateOf<NgoProfile?>(null) }

    // Theme Colors
    val bgColor = Color(0xFFF8FAFC)
    val cardBg = Color(0xFFFFFFFF)
    val primaryPurple = Color(0xFF6C3EF4)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)
    val editBtnBg = Color(0xFFEDE9FE)
    val editBtnText = Color(0xFF6C3EF4)
    val logoutBtnBg = Color(0xFFFEE2E2)
    val logoutTextColor = Color(0xFFDC2626)

    LaunchedEffect(Unit) {
        getFirebaseIdToken(
            onTokenReceived = { token ->
                scope.launch {
                    try {
                        val authHeader = "Bearer $token"
                        val res = RetrofitClient.apiService.getNgoProfile(authHeader)
                        Log.d("NgoProfileScreen", "GET /api/ngos/profile code=${res.code()}")

                        if (res.isSuccessful && res.body() != null) {
                            ngo = res.body()
                        } else if (res.code() == 404) {
                            Log.d("NgoProfileScreen", "NGO profile not found, redirecting to edit")
                            navController.navigate("ngo-profile-edit") {
                                popUpTo("ngo-profile") { inclusive = true }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("NgoProfileScreen", "Error loading profile", e)
                    } finally {
                        loading = false
                    }
                }
            },
            onError = { loading = false }
        )
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 2.dp,
                color = Color.White
            ) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                    title = {
                        Image(
                            painter = painterResource(id = R.drawable.eventeats_logo),
                            contentDescription = "Logo",
                            modifier = Modifier.height(32.dp)
                        )
                    },
                    actions = {
                        IconButton(onClick = { /* TODO: Notification screen */ }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = textPrimary
                            )
                        }
                    }
                )
            }
        }
    ) { padding ->

        AnimatedContent(
            targetState = loading,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "loadingTransition"
        ) { isLoading ->
            if (isLoading) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryPurple)
                }
            } else {
                val profile = ngo
                if (profile != null) {
                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Spacer(modifier = Modifier.height(24.dp))

                        // --- PROFILE CARD ---
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = cardBg,
                            shadowElevation = 6.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(contentAlignment = Alignment.BottomEnd) {
                                    Image(
                                        painter = rememberAsyncImagePainter(model = profile.imageUrl),
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFF1F5F9))
                                            .clickable { /* TODO: Image upload option */ },
                                        contentScale = ContentScale.Crop
                                    )
                                    // NGO Verification Badge
                                    Surface(
                                        shape = CircleShape,
                                        color = Color.White,
                                        modifier = Modifier.size(24.dp),
                                        shadowElevation = 2.dp
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Verified",
                                            tint = Color(0xFF10B981),
                                            modifier = Modifier.padding(2.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = profile.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                    color = textPrimary
                                )
                                Text(
                                    text = profile.email,
                                    fontSize = 14.sp,
                                    color = textSecondary
                                )
                                Text(
                                    text = profile.phone,
                                    fontSize = 14.sp,
                                    color = textSecondary
                                )
                                Spacer(Modifier.height(16.dp))
                                Button(
                                    onClick = { navController.navigate("ngo-profile-edit") },
                                    colors = ButtonDefaults.buttonColors(containerColor = editBtnBg),
                                    shape = RoundedCornerShape(50),
                                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                                ) {
                                    Text("Edit Profile", color = editBtnText, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // --- MENU SECTION ---
                        MenuCardItem(
                            title = "My Events",
                            icon = Icons.Default.CalendarMonth,
                            primaryColor = primaryPurple,
                            textPrimary = textPrimary,
                            onClick = { /* TODO: Navigate to My Events */ }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        MenuCardItem(
                            title = "Documents",
                            icon = Icons.Default.Description,
                            primaryColor = primaryPurple,
                            textPrimary = textPrimary,
                            onClick = { navController.navigate("ngo-documents-list") }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        MenuCardItem(
                            title = "Help & Support",
                            icon = Icons.Default.Help,
                            primaryColor = primaryPurple,
                            textPrimary = textPrimary,
                            onClick = { /* TODO: Help screen */ }
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        // --- LOGOUT ---
                        Button(
                            onClick = {
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = logoutBtnBg),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Logout",
                                color = logoutTextColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuCardItem(
    title: String,
    icon: ImageVector,
    primaryColor: Color,
    textPrimary: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(primaryColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textPrimary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color(0xFF94A3B8)
            )
        }
    }
}

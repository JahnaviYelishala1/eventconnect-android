package com.example.eventconnect.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

    LaunchedEffect(Unit) {
        getFirebaseIdToken(
            onTokenReceived = { token ->
                scope.launch {
                    try {
                        val res = RetrofitClient.apiService.getNgoProfile("Bearer $token")
                        if (res.isSuccessful && res.body() != null) {
                            ngo = res.body()
                        } else if (res.code() == 404) {
                            navController.navigate("ngo-profile-edit") {
                                popUpTo("ngo-profile") { inclusive = true }
                            }
                        }
                    } finally {
                        loading = false
                    }
                }
            },
            onError = { loading = false }
        )
    }

    Scaffold(
        containerColor = Color(0xFFFAF8F0),
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
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                }
            )
        }
    ) { padding ->

        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val profile = ngo ?: return@Scaffold

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            // --- USER INFO ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = profile.imageUrl),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(profile.name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                    Text(profile.email, fontSize = 14.sp, color = Color.DarkGray)
                    Text(profile.phone, fontSize = 14.sp, color = Color.DarkGray)
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { navController.navigate("ngo-profile-edit") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC8C8C8)),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Text("Edit Profile", color = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Divider(color = Color.LightGray)

            // --- MENU ITEMS ---
            ProfileMenuItem("My Events") { /* TODO: Navigate to My Events */ }
            ProfileMenuItem("Documents") { navController.navigate("ngo-documents-list") }
            ProfileMenuItem("Help & Support") { /* TODO: Help screen */ }

            Spacer(modifier = Modifier.height(24.dp))

            // --- LOGOUT ---
            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") { // Assuming 'login' is your login route
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC8C8C8))
            ) {
                Text("Logout", color = Color.Black)
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(title: String, onClick: () -> Unit) {
    Column(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontSize = 18.sp, color = Color.Black)
            Icon(Icons.Default.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(16.dp))
        }
        Divider(color = Color.LightGray)
    }
}

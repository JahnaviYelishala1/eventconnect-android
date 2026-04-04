package com.example.eventconnect.ui.auth

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventconnect.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RoleSelectionScreen(
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    var error by remember { mutableStateOf<String?>(null) }
    var selectedRole by remember { mutableStateOf<RoleOption?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Colors
    val bgColor = Color(0xFFF8FAFC)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)
    val primaryColor = Color(0xFF6C3EF4)
    val primaryGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF7C3AED), Color(0xFF6C3EF4))
    )

    val roles = listOf(
        RoleOption(
            id = "event_organizer",
            title = "Event Organizer",
            description = "Create events & donate surplus food",
            icon = Icons.Default.CalendarToday,
            route = "organizer-home"
        ),
        RoleOption(
            id = "caterer",
            title = "Caterer",
            description = "Manage food preparation & surplus",
            icon = Icons.Default.Restaurant,
            route = "caterer-home"
        ),
        RoleOption(
            id = "ngo",
            title = "NGO",
            description = "Receive and collect surplus food",
            icon = Icons.Default.Handshake,
            route = "ngo-home"
        )
    )

    fun handleRoleSelection() {
        val selection = selectedRole ?: return
        isLoading = true
        error = null
        
        getFirebaseIdToken(
            onTokenReceived = { token ->
                if (token.isBlank()) {
                    error = "Authentication token not found"
                    isLoading = false
                    return@getFirebaseIdToken
                }

                scope.launch(Dispatchers.IO) {
                    try {
                        val response = RetrofitClient.apiService.selectRole(
                            role = selection.id,
                            token = "Bearer $token"
                        )

                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                navController.navigate(selection.route) {
                                    popUpTo("role-selection") { inclusive = true }
                                }
                            } else {
                                error = "Failed to select role: ${response.code()}"
                                isLoading = false
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            error = e.localizedMessage ?: "An unexpected error occurred"
                            isLoading = false
                        }
                    }
                }
            },
            onError = {
                error = it
                isLoading = false
            }
        )
    }

    Scaffold(
        containerColor = bgColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // --- HEADER ---
            Text(
                text = "Select Your Role",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Choose how you want to use EventConnect",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = textSecondary
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // --- ROLE CARDS ---
            roles.forEach { role ->
                RoleCard(
                    role = role,
                    isSelected = selectedRole == role,
                    onClick = { selectedRole = role },
                    primaryColor = primaryColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- ERROR MESSAGE ---
            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // --- CONTINUE BUTTON ---
            Button(
                onClick = { handleRoleSelection() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp), clip = false),
                enabled = selectedRole != null && !isLoading,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (selectedRole != null && !isLoading) primaryGradient else Brush.linearGradient(listOf(Color.LightGray, Color.LightGray)),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Continue",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun RoleCard(
    role: RoleOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    val cardBg = if (isSelected) Color(0xFFF5F3FF) else Color.White
    val borderColor = if (isSelected) primaryColor else Color.Transparent

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isSelected) 0.dp else 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0x1A000000)
            )
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        color = cardBg
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        if (isSelected) primaryColor.copy(alpha = 0.1f) else Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = role.icon,
                    contentDescription = null,
                    tint = if (isSelected) primaryColor else textSecondary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = role.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = role.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = textSecondary,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}

data class RoleOption(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String
)

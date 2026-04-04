package com.example.eventconnect.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eventconnect.data.network.AdminNgoDocument
import com.example.eventconnect.data.network.AdminNgoResponse
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNgoReviewScreen(
    navController: NavController,
    viewModel: AdminNgoViewModel = viewModel()
) {
    val ngos by viewModel.ngos.collectAsState()
    val error by viewModel.error.collectAsState()
    val uriHandler = LocalUriHandler.current

    // Theme Colors
    val bgColor = Color(0xFFF8FAFC)
    val primaryPurple = Color(0xFF6C3EF4)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)
    val successGreen = Color(0xFF22C55E)
    val errorRed = Color(0xFFEF4444)

    LaunchedEffect(Unit) {
        viewModel.loadNgos()
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            Surface(shadowElevation = 2.dp) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                    title = {
                        Text(
                            "NGO Verification",
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    },
                    actions = {
                        IconButton(onClick = { viewModel.loadNgos() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = textPrimary)
                        }
                        IconButton(onClick = {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = textPrimary)
                        }
                    }
                )
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                error != null -> {
                    Text(
                        text = error ?: "An unexpected error occurred",
                        color = errorRed,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                ngos.isEmpty() -> {
                    CircularProgressIndicator(
                        color = primaryPurple,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(ngos) { ngo ->
                            NgoAdminCard(
                                ngo = ngo,
                                primaryPurple = primaryPurple,
                                textPrimary = textPrimary,
                                textSecondary = textSecondary,
                                onApprove = { viewModel.approveDocument(it) },
                                onReject = { viewModel.rejectDocument(it) },
                                onViewDoc = { uriHandler.openUri(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NgoAdminCard(
    ngo: AdminNgoResponse,
    primaryPurple: Color,
    textPrimary: Color,
    textSecondary: Color,
    onApprove: (Int) -> Unit,
    onReject: (Int) -> Unit,
    onViewDoc: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0x1A000000)),
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Name and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ngo.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                )
                StatusBadge(status = ngo.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Info Fields
            InfoRow(label = "Reg No:", value = ngo.registration_number, textSecondary = textSecondary, textPrimary = textPrimary)
            InfoRow(label = "Status:", value = ngo.status, textSecondary = textSecondary, textPrimary = textPrimary)

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(16.dp))

            // Documents Section
            Text(
                text = "Documents",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = textPrimary)
            )

            Spacer(modifier = Modifier.height(12.dp))

            ngo.documents.forEach { doc ->
                DocumentItem(
                    doc = doc,
                    primaryPurple = primaryPurple,
                    textPrimary = textPrimary,
                    onApprove = { onApprove(doc.id) },
                    onReject = { onReject(doc.id) },
                    onViewDoc = { onViewDoc(doc.file_url) }
                )
                if (doc != ngo.documents.last()) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun DocumentItem(
    doc: AdminNgoDocument,
    primaryPurple: Color,
    textPrimary: Color,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onViewDoc: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F5F9).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = doc.document_type,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = textPrimary)
            )
            Spacer(modifier = Modifier.height(4.dp))
            StatusBadge(status = doc.status, small = true)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "View Document",
                color = primaryPurple,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { onViewDoc() }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clickable(enabled = doc.status == "PENDING") { onApprove() },
                shape = CircleShape,
                color = if (doc.status == "PENDING") Color(0xFF22C55E) else Color.LightGray.copy(alpha = 0.5f)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Approve",
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clickable(enabled = doc.status == "PENDING") { onReject() },
                shape = CircleShape,
                color = if (doc.status == "PENDING") Color(0xFFEF4444) else Color.LightGray.copy(alpha = 0.5f)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Reject",
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, textSecondary: Color, textPrimary: Color) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(text = "$label ", color = textSecondary, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, color = textPrimary, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun StatusBadge(status: String, small: Boolean = false) {
    val (bgColor, textColor) = when (status) {
        "VERIFIED", "APPROVED" -> Color(0xFFDCFCE7) to Color(0xFF166534)
        "PENDING" -> Color(0xFFFEF3C7) to Color(0xFF92400E)
        "REJECTED" -> Color(0xFFFEE2E2) to Color(0xFF991B1B)
        else -> Color.LightGray.copy(alpha = 0.2f) to Color.DarkGray
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(if (small) 8.dp else 12.dp)
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = if (small) 10.sp else 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = if (small) 8.dp else 12.dp, vertical = if (small) 2.dp else 4.dp)
        )
    }
}

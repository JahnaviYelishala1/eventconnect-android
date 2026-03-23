package com.example.eventconnect.ui.chat

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    requestId: Int,
    currentUserId: Int? = null,
    onBack: (() -> Unit)? = null
) {
    val viewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(requestId)
    )

    val messages by viewModel.messages.collectAsState()
    val viewModelUserId by viewModel.currentUserId.collectAsState()
    var text by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val effectiveUserId = currentUserId ?: viewModelUserId

    LaunchedEffect(Unit) {
        viewModel.loadHistoryAndConnect()
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFEDE9FE),
            Color(0xFFC4B5FD),
            Color.White
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Chat",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A1C1E)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { onBack?.invoke() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1A1C1E))
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Info, contentDescription = "Info", tint = Color(0xFF1A1C1E))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            bottomBar = {
                ChatInputArea(
                    text = text,
                    onTextChange = { text = it },
                    onSend = {
                        if (text.isNotBlank()) {
                            viewModel.sendMessage(text)
                            text = ""
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (effectiveUserId == null || effectiveUserId == 0) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF7C3AED))
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(messages) { index, msg ->
                            val isMe = msg.sender_id == effectiveUserId
                            ChatBubble(
                                message = msg.message,
                                timestamp = msg.timestamp,
                                isMe = isMe
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: String,
    timestamp: String,
    isMe: Boolean
) {
    val incomingColor = Color(0xFFF3F4F6)
    val outgoingColor = Color(0xFFBFDBFE)
    val textColor = Color(0xFF1A1C1E)
    val secondaryText = Color(0xFF6B7280)

    val alignment = if (isMe) Alignment.End else Alignment.Start
    val shape = if (isMe) {
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    } else {
        RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = if (isMe) outgoingColor else incomingColor,
            shape = shape,
            modifier = Modifier
                .widthIn(max = 280.dp)
                .shadow(2.dp, shape),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message,
                    color = textColor,
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                )
            }
        }
        Text(
            text = formatTimeChat(timestamp),
            fontSize = 10.sp,
            color = secondaryText,
            modifier = Modifier.padding(top = 4.dp, start = if (isMe) 0.dp else 4.dp, end = if (isMe) 4.dp else 0.dp)
        )
    }
}

@Composable
fun ChatInputArea(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(
        color = Color.White.copy(alpha = 0.9f),
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(10.dp, RoundedCornerShape(32.dp))
            .clip(RoundedCornerShape(32.dp)),
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text("Type a message...", color = Color(0xFF94A3B8)) },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF1F5F9),
                    unfocusedContainerColor = Color(0xFFF1F5F9),
                    disabledContainerColor = Color(0xFFF1F5F9),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color(0xFF1A1C1E),
                    unfocusedTextColor = Color(0xFF1A1C1E)
                ),
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(Modifier.width(8.dp))

            val sendGradient = Brush.verticalGradient(listOf(Color(0xFF9F5FFF), Color(0xFF7C3AED)))
            
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(sendGradient)
                    .clickable { onSend() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

fun formatTimeChat(timestamp: String): String {
    if (timestamp.isEmpty()) return ""
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        try {
            val formatted = if (timestamp.contains("T") || timestamp.contains("+")) {
                val parsed = java.time.OffsetDateTime.parse(timestamp)
                parsed.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"))
            } else {
                val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
                val parsed = java.time.LocalDateTime.parse(timestamp, formatter)
                parsed.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"))
            }
            formatted
        } catch (_: Exception) {
            ""
        }
    } else {
        ""
    }
}

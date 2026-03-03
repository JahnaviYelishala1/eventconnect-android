package com.example.eventconnect.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ChatScreen(
    bookingId: Int
) {
    val viewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(bookingId)
    )

    val messages by viewModel.messages.collectAsState()
    var text by remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
        viewModel.connectSocket()
    }

    // ✅ Auto scroll when new message arrives
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(12.dp)
        ) {

            itemsIndexed(messages) { index, msg ->

                val isMe = msg.sender_id == viewModel.currentUserId

                ChatBubble(
                    message = msg.message,
                    timestamp = msg.timestamp,
                    isMe = isMe
                )

                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        Divider()

        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") }
            )

            Spacer(Modifier.width(8.dp))

            Button(
                onClick = {
                    viewModel.sendMessage(text)
                    text = ""
                }
            ) {
                Text("Send")
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

    val bubbleColor =
        if (isMe)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.surfaceVariant

    val textColor =
        if (isMe)
            MaterialTheme.colorScheme.onPrimary
        else
            MaterialTheme.colorScheme.onSurface

    val alignment =
        if (isMe) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = alignment
    ) {

        Surface(
            color = bubbleColor,
            shape = if (isMe)
                MaterialTheme.shapes.medium.copy(
                    topEnd = androidx.compose.foundation.shape.ZeroCornerSize
                )
            else
                MaterialTheme.shapes.medium.copy(
                    topStart = androidx.compose.foundation.shape.ZeroCornerSize
                )
        ) {

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Text(
                    text = message,
                    color = textColor
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = formatTime(timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

fun formatTime(timestamp: String): String {
    return try {
        val parsed = OffsetDateTime.parse(timestamp)
        parsed.format(DateTimeFormatter.ofPattern("hh:mm a"))
    } catch (e: Exception) {
        ""
    }
}
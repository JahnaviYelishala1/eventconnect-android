package com.example.eventconnect.utils

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

suspend fun getAuthHeader(): String? {
    val user = FirebaseAuth.getInstance().currentUser ?: return null
    val token = user.getIdToken(true).await().token ?: return null
    return "Bearer $token"
}


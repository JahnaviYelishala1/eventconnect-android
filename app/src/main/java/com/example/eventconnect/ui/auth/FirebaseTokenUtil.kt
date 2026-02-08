package com.example.eventconnect.ui.auth

import com.google.firebase.auth.FirebaseAuth

fun getFirebaseIdToken(
    onTokenReceived: (String) -> Unit,
    onError: (String) -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser

    if (user == null) {
        onError("User not logged in")
        return
    }

    user.getIdToken(true)
        .addOnSuccessListener { result ->
            onTokenReceived(result.token!!)
        }
        .addOnFailureListener { exception ->
            onError(exception.message ?: "Token error")
        }
}

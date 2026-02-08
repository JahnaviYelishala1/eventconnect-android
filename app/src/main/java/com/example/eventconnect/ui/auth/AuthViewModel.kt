package com.example.eventconnect.ui.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<String?>(null)
    val authState: StateFlow<String?> = _authState

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _authState.value = "SUCCESS"
            }
            .addOnFailureListener { exception ->
                _authState.value = exception.message
            }
    }

    fun signup(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _authState.value = "SUCCESS"
            }
            .addOnFailureListener { exception ->
                _authState.value = exception.message
            }
    }
}

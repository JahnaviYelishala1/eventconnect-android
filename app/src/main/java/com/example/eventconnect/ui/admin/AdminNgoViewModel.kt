package com.example.eventconnect.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.network.AdminNgoResponse
import com.example.eventconnect.data.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AdminNgoViewModel : ViewModel() {

    private val _ngos = MutableStateFlow<List<AdminNgoResponse>>(emptyList())
    val ngos: StateFlow<List<AdminNgoResponse>> = _ngos

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private suspend fun getToken(): String =
        suspendCancellableCoroutine { cont ->
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                cont.resumeWithException(Exception("User not logged in"))
                return@suspendCancellableCoroutine
            }

            user.getIdToken(true)
                .addOnSuccessListener { result ->
                    cont.resume(result.token!!)
                }
                .addOnFailureListener { e ->
                    cont.resumeWithException(e)
                }
        }

    fun loadNgos() {
        viewModelScope.launch {
            try {
                val token = getToken()
                val res = RetrofitClient.apiService
                    .getAllNgos("Bearer $token")

                if (res.isSuccessful) {
                    _ngos.value = res.body() ?: emptyList()
                } else {
                    _error.value = res.errorBody()?.string()
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // ✅ Update document status locally (instant UI update)
    private fun updateDocumentStatus(docId: Int, newStatus: String) {
        _ngos.value = _ngos.value.map { ngo ->
            ngo.copy(
                documents = ngo.documents.map { doc ->
                    if (doc.id == docId) {
                        doc.copy(status = newStatus)
                    } else doc
                }
            )
        }
    }

    fun approveDocument(docId: Int) {
        viewModelScope.launch {
            try {
                val token = getToken()
                RetrofitClient.apiService
                    .approveDocument("Bearer $token", docId)

                updateDocumentStatus(docId, "APPROVED")
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun rejectDocument(docId: Int) {
        viewModelScope.launch {
            try {
                val token = getToken()
                RetrofitClient.apiService
                    .rejectDocument("Bearer $token", docId)

                updateDocumentStatus(docId, "REJECTED")
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}

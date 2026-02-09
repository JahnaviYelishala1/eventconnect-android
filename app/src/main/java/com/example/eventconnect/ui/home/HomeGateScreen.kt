package com.example.eventconnect.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.eventconnect.data.network.RetrofitClient
import com.example.eventconnect.data.network.NgoMeResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun HomeGateScreen(navController: NavController) {

    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf("Checking session...") }
    var checked by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {

        if (checked) return@LaunchedEffect
        checked = true

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser == null) {
            navController.navigate("login") {
                popUpTo("home-gate") { inclusive = true }
            }
            return@LaunchedEffect
        }

        firebaseUser.getIdToken(true)
            .addOnSuccessListener { result ->
                val token = result.token ?: return@addOnSuccessListener

                scope.launch {
                    try {
                        val response =
                            RetrofitClient.apiService.protectedCall("Bearer $token")

                        if (!response.isSuccessful || response.body() == null) {
                            status = "Authentication failed"
                            return@launch
                        }

                        val user = response.body()!!
                        val role = user.role

                        when (role) {
                            "null"-> {
                                println("🔥 ROLE FROM BACKEND = $role")
                            }



                                "admin" -> {
                                navController.navigate("admin-ngo-review") {
                                    popUpTo("home-gate") { inclusive = true }
                                }
                            }

                            "event_organizer" -> {
                                navController.navigate("organizer-home") {
                                    popUpTo("home-gate") { inclusive = true }
                                }
                            }

                            "caterer" -> {
                                navController.navigate("caterer-home") {
                                    popUpTo("home-gate") { inclusive = true }
                                }
                            }

                            "ngo" -> {
                                val ngoRes =
                                    RetrofitClient.apiService.getMyNgo("Bearer $token")

                                if (!ngoRes.isSuccessful || ngoRes.body() == null) {
                                    navController.navigate("ngo-register") {
                                        popUpTo("home-gate") { inclusive = true }
                                    }
                                    return@launch
                                }

                                val ngo: NgoMeResponse = ngoRes.body()!!

                                when {
                                    !ngo.exists -> navController.navigate("ngo-register")
                                    !ngo.documents_uploaded -> navController.navigate("ngo-documents")
                                    else -> navController.navigate("ngo-home")
                                }
                            }

                            else -> {
                                navController.navigate("role-selection") {
                                    popUpTo("home-gate") { inclusive = true }
                                }
                            }
                        }

                    } catch (e: Exception) {
                        status = "Network error"
                    }
                }
            }
            .addOnFailureListener {
                status = "Token error"
            }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(status)
    }
}

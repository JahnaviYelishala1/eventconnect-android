package com.example.eventconnect.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventconnect.ui.auth.LoginScreen
import com.example.eventconnect.ui.auth.RoleSelectionScreen
import com.example.eventconnect.ui.auth.SignupScreen
import com.example.eventconnect.ui.home.HomeGateScreen
import com.example.eventconnect.ui.home.OrganizerHomeScreen
import com.example.eventconnect.ui.home.CatererHomeScreen
import com.example.eventconnect.ui.home.CreateEventScreen
import com.example.eventconnect.ui.home.MyEventsScreen
import com.example.eventconnect.ui.home.NgoHomeScreen
import com.example.eventconnect.ui.admin.AdminNgoReviewScreen
import com.example.eventconnect.ui.ngo.NgoRegistrationScreen
import com.example.eventconnect.ui.ngo.NgoDocumentUploadScreen
import com.example.eventconnect.ui.ngo.NgoDocumentsScreen


@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // -------- AUTH SCREENS --------
        composable("login") {
            LoginScreen(
                onNavigateToSignup = {
                    navController.navigate("signup")
                },
                onLoginSuccess = {
                    navController.navigate("home-gate") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("signup") {
            SignupScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onSignupSuccess = {
                    navController.navigate("home-gate") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        // -------- ROLE CHECK GATE --------
        composable("home-gate") {
            HomeGateScreen(navController)
        }

        // -------- ROLE SELECTION --------
        composable("role-selection") {
            RoleSelectionScreen(navController)
        }

        // -------- ROLE-SPECIFIC HOMES --------
        composable("organizer-home") {
            OrganizerHomeScreen(navController)   // ✅ FIX
        }

        composable("caterer-home") {
            CatererHomeScreen(navController)     // ✅ FIX
        }

        composable("ngo-home") {
            NgoHomeScreen(navController)         // ✅ FIX
        }

        composable("create-event") {
            CreateEventScreen(navController)
        }

        composable("my-events") {
            MyEventsScreen(navController)
        }

        composable("admin-ngo-review") {
            AdminNgoReviewScreen(navController)
        }

        composable("ngo-register") {
            NgoRegistrationScreen(navController)
        }

        composable("ngo-documents") {
            NgoDocumentUploadScreen(navController)
        }

        composable("ngo-documents-list") {
            NgoDocumentsScreen(navController)
        }

    }
}

package com.example.eventconnect.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.eventconnect.ui.auth.LoginScreen
import com.example.eventconnect.ui.auth.RoleSelectionScreen
import com.example.eventconnect.ui.auth.SignupScreen
import com.example.eventconnect.ui.home.*
import com.example.eventconnect.ui.admin.AdminNgoReviewScreen
import com.example.eventconnect.ui.caterer.CatererProfileScreen
import com.example.eventconnect.ui.ngo.NgoRegistrationScreen
import com.example.eventconnect.ui.ngo.NgoDocumentUploadScreen
import com.example.eventconnect.ui.ngo.NgoDocumentsScreen
import com.example.eventconnect.ui.profile.NgoProfileEditScreen
import com.example.eventconnect.ui.profile.NgoProfileScreen

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("ngo-home", Icons.Default.Home, "Home")
    object Profile : BottomNavItem("ngo-profile", Icons.Default.AccountCircle, "Profile")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomNavItems.map { it.route }) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = { navController.navigate(item.route) },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(it)
        ) {

            // -------- AUTH SCREENS --------
            composable("login") {
                LoginScreen(
                    onNavigateToSignup = { navController.navigate("signup") },
                    onLoginSuccess = { navController.navigate("home-gate") { popUpTo("login") { inclusive = true } } }
                )
            }

            composable("signup") {
                SignupScreen(
                    onNavigateToLogin = { navController.popBackStack() },
                    onSignupSuccess = { navController.navigate("home-gate") { popUpTo("signup") { inclusive = true } } }
                )
            }

            // -------- ROLE CHECK GATE --------
            composable("home-gate") { HomeGateScreen(navController) }

            // -------- ROLE SELECTION --------
            composable("role-selection") { RoleSelectionScreen(navController) }

            // -------- ROLE-SPECIFIC HOMES --------
            composable("organizer-home") { OrganizerHomeScreen(navController) }
            composable("caterer-home") { CatererHomeScreen(navController) }
            composable(BottomNavItem.Home.route) { NgoHomeScreen(navController) }
            composable("create-event") { CreateEventScreen(navController) }
            composable("my-events") { MyEventsScreen(navController) }
            composable("admin-ngo-review") { AdminNgoReviewScreen(navController) }
            composable("ngo-register") { NgoRegistrationScreen(navController) }
            composable("ngo-documents") { NgoDocumentUploadScreen(navController) }
            composable("ngo-documents-list") { NgoDocumentsScreen(navController) }
            composable(BottomNavItem.Profile.route) { NgoProfileScreen(navController) }
            composable("ngo-profile-edit") { NgoProfileEditScreen(navController) }
            composable("create-caterer-profile") {
                CatererProfileScreen(navController)
            }

            composable("caterer-profile") {
                CatererProfileScreen(navController)
            }

            composable(
                route = "find-caterer/{eventId}",
                arguments = listOf(
                    navArgument("eventId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val eventId = backStackEntry.arguments
                    ?.getInt("eventId") ?: 0

                FindCatererScreen(
                    navController = navController,
                    eventId = eventId
                )
            }
        }
    }
}

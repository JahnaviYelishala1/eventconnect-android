package com.example.eventconnect.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.eventconnect.ui.auth.*
import com.example.eventconnect.ui.home.*
import com.example.eventconnect.ui.admin.AdminNgoReviewScreen
import com.example.eventconnect.ui.ngo.*
import com.example.eventconnect.ui.profile.*
import com.google.firebase.auth.FirebaseAuth

/* -------------------------------------------------- */
/* ---------------- BOTTOM NAV ITEMS ---------------- */
/* -------------------------------------------------- */

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {

    // Organizer
    object OrganizerHome :
        BottomNavItem("organizer-home", Icons.Default.Home, "Home")

    object OrganizerEvents :
        BottomNavItem("my-events", Icons.Default.List, "My Events")

    object OrganizerCreateEvent :
        BottomNavItem("create-event", Icons.Default.Add, "Create")

    object OrganizerProfile :
        BottomNavItem("organizer-profile", Icons.Default.AccountCircle, "Profile")

    // Caterer
    object CatererHome :
        BottomNavItem("caterer-home", Icons.Default.Home, "Home")

    object CatererBookings :
        BottomNavItem("caterer-bookings", Icons.Default.List, "Bookings")

    object CatererProfile :
        BottomNavItem("caterer-profile", Icons.Default.AccountCircle, "Profile")

    // NGO
    object NgoHome :
        BottomNavItem("ngo-home", Icons.Default.Home, "Home")

    object NgoProfile :
        BottomNavItem("ngo-profile", Icons.Default.AccountCircle, "Profile")
}

/* ------------------- NAV GRAPH -------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph() {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val organizerRoutes = listOf(
        BottomNavItem.OrganizerHome.route,
        BottomNavItem.OrganizerEvents.route,
        BottomNavItem.OrganizerCreateEvent.route,
        BottomNavItem.OrganizerProfile.route
    )

    val catererRoutes = listOf(
        BottomNavItem.CatererHome.route,
        BottomNavItem.CatererBookings.route,
        BottomNavItem.CatererProfile.route
    )

    val ngoRoutes = listOf(
        BottomNavItem.NgoHome.route,
        BottomNavItem.NgoProfile.route
    )

    Scaffold(
        bottomBar = {

            fun navigateTo(route: String) {
                navController.navigate(route) {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                }
            }

            when {

                /* ---------------- ORGANIZER ---------------- */

                currentRoute in organizerRoutes -> {
                    NavigationBar {
                        listOf(
                            BottomNavItem.OrganizerHome,
                            BottomNavItem.OrganizerEvents,
                            BottomNavItem.OrganizerCreateEvent,
                            BottomNavItem.OrganizerProfile
                        ).forEach { item ->
                            NavigationBarItem(
                                selected = currentRoute == item.route,
                                onClick = { navigateTo(item.route) },
                                icon = { Icon(item.icon, null) },
                                label = { Text(item.label) }
                            )
                        }
                    }
                }

                /* ---------------- CATERER ---------------- */

                currentRoute in catererRoutes -> {
                    NavigationBar {

                        NavigationBarItem(
                            selected = currentRoute == BottomNavItem.CatererHome.route,
                            onClick = { navigateTo(BottomNavItem.CatererHome.route) },
                            icon = { Icon(Icons.Default.Home, null) },
                            label = { Text("Home") }
                        )

                        NavigationBarItem(
                            selected = currentRoute == BottomNavItem.CatererBookings.route,
                            onClick = { navigateTo(BottomNavItem.CatererBookings.route) },
                            icon = { Icon(Icons.Default.List, null) },
                            label = { Text("Bookings") }
                        )

                        NavigationBarItem(
                            selected = currentRoute == BottomNavItem.CatererProfile.route,
                            onClick = { navigateTo(BottomNavItem.CatererProfile.route) },
                            icon = { Icon(Icons.Default.AccountCircle, null) },
                            label = { Text("Profile") }
                        )

                        NavigationBarItem(
                            selected = false,
                            onClick = {
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            icon = { Icon(Icons.Default.ExitToApp, null) },
                            label = { Text("Logout") }
                        )
                    }
                }

                /* ---------------- NGO ---------------- */

                currentRoute in ngoRoutes -> {
                    NavigationBar {
                        listOf(
                            BottomNavItem.NgoHome,
                            BottomNavItem.NgoProfile
                        ).forEach { item ->
                            NavigationBarItem(
                                selected = currentRoute == item.route,
                                onClick = { navigateTo(item.route) },
                                icon = { Icon(item.icon, null) },
                                label = { Text(item.label) }
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(padding)
        ) {

            /* ---------------- AUTH ---------------- */

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
                    onNavigateToLogin = { navController.popBackStack() },
                    onSignupSuccess = {
                        navController.navigate("home-gate") {
                            popUpTo("signup") { inclusive = true }
                        }
                    }
                )
            }

            composable("home-gate") {
                HomeGateScreen(navController)
            }

            composable("role-selection") {
                RoleSelectionScreen(navController)
            }

            /* ---------------- ORGANIZER ---------------- */

            composable(BottomNavItem.OrganizerHome.route) {
                OrganizerHomeScreen(navController)
            }

            composable(BottomNavItem.OrganizerEvents.route) {
                MyEventsScreen(navController)
            }

            composable(BottomNavItem.OrganizerCreateEvent.route) {
                CreateEventScreen(navController)
            }

            composable(BottomNavItem.OrganizerProfile.route) {
                OrganizerProfileScreen(navController)
            }

            /* ---------------- CATERER ---------------- */

            composable(BottomNavItem.CatererHome.route) {
                CatererHomeScreen(navController)
            }


            composable(BottomNavItem.CatererProfile.route) {
                CatererProfileScreen(navController)
            }

            /* ---------------- NGO ---------------- */

            composable(BottomNavItem.NgoHome.route) {
                NgoHomeScreen(navController)
            }

            composable(BottomNavItem.NgoProfile.route) {
                NgoProfileScreen(navController)
            }

            composable("ngo-profile-edit") {
                NgoProfileEditScreen(navController)
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

            /* ---------------- ADMIN ---------------- */

            composable("admin-ngo-review") {
                AdminNgoReviewScreen(navController)
            }
        }
    }
}
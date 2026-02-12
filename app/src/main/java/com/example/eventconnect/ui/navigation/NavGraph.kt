package com.example.eventconnect.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.eventconnect.ui.auth.LoginScreen
import com.example.eventconnect.ui.auth.RoleSelectionScreen
import com.example.eventconnect.ui.auth.SignupScreen
import com.example.eventconnect.ui.home.*
import com.example.eventconnect.ui.admin.AdminNgoReviewScreen
import com.example.eventconnect.ui.ngo.*
import com.example.eventconnect.ui.profile.*

/* -------------------------------------------------- */
/* ---------------- BOTTOM NAV ITEMS ---------------- */
/* -------------------------------------------------- */

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {

    // Organizer
    object OrganizerHome : BottomNavItem("organizer-home", Icons.Default.Home, "Home")
    object OrganizerEvents : BottomNavItem("my-events", Icons.Default.List, "My Events")
    object OrganizerProfile : BottomNavItem("organizer-profile", Icons.Default.AccountCircle, "Profile")

    // Caterer
    object CatererHome : BottomNavItem("caterer-home", Icons.Default.Home, "Home")
    object CatererBookings : BottomNavItem("caterer-bookings", Icons.Default.List, "Bookings")
    object CatererProfile : BottomNavItem("caterer-profile", Icons.Default.AccountCircle, "Profile")

    // NGO
    object NgoHome : BottomNavItem("ngo-home", Icons.Default.Home, "Home")
    object NgoProfile : BottomNavItem("ngo-profile", Icons.Default.AccountCircle, "Profile")
}

/* -------------------------------------------------- */
/* ------------------- NAV GRAPH -------------------- */
/* -------------------------------------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph() {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val organizerRoutes = listOf(
        BottomNavItem.OrganizerHome.route,
        BottomNavItem.OrganizerEvents.route,
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

            when {
                currentRoute in organizerRoutes -> {
                    NavigationBar {
                        listOf(
                            BottomNavItem.OrganizerHome,
                            BottomNavItem.OrganizerEvents,
                            BottomNavItem.OrganizerProfile
                        ).forEach { item ->
                            NavigationBarItem(
                                selected = currentRoute == item.route,
                                onClick = { navController.navigate(item.route) },
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) }
                            )
                        }
                    }
                }

                currentRoute in catererRoutes -> {
                    NavigationBar {
                        listOf(
                            BottomNavItem.CatererHome,
                            BottomNavItem.CatererBookings,
                            BottomNavItem.CatererProfile
                        ).forEach { item ->
                            NavigationBarItem(
                                selected = currentRoute == item.route,
                                onClick = { navController.navigate(item.route) },
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) }
                            )
                        }
                    }
                }

                currentRoute in ngoRoutes -> {
                    NavigationBar {
                        listOf(
                            BottomNavItem.NgoHome,
                            BottomNavItem.NgoProfile
                        ).forEach { item ->
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
                    onNavigateToSignup = { navController.navigate("signup") },
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

            /* ---------------- ORGANIZER FLOW ---------------- */

            composable(BottomNavItem.OrganizerHome.route) {
                OrganizerHomeScreen(navController)
            }

            composable(BottomNavItem.OrganizerEvents.route) {
                MyEventsScreen(navController)
            }

            composable("create-event") {
                CreateEventScreen(navController)
            }

            composable(
                route = "find-caterer/{eventId}",
                arguments = listOf(navArgument("eventId") {
                    type = NavType.IntType
                })
            ) { backStackEntry ->

                val eventId = backStackEntry.arguments?.getInt("eventId") ?: 0

                FindCatererScreen(
                    navController = navController,
                    eventId = eventId
                )
            }

            composable(
                route = "booking-status/{eventId}",
                arguments = listOf(navArgument("eventId") {
                    type = NavType.IntType
                })
            ) { backStackEntry ->

                val eventId = backStackEntry.arguments?.getInt("eventId") ?: 0

                BookingStatusScreen(
                    navController = navController,
                    eventId = eventId
                )
            }

            composable(BottomNavItem.OrganizerProfile.route) {
                OrganizerProfileScreen(navController)
            }

            /* ---------------- CATERER FLOW ---------------- */

            composable(BottomNavItem.CatererHome.route) {
                CatererHomeScreen(navController)
            }

            composable(BottomNavItem.CatererBookings.route) {
                CatererBookingsScreen(navController)
            }

            composable(BottomNavItem.CatererProfile.route) {
                CatererProfileScreen(navController)
            }

            composable("create-caterer-profile") {
                CatererProfileScreen(navController)
            }

            /* ---------------- NGO FLOW ---------------- */

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

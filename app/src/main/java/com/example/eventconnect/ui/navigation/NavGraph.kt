package com.example.eventconnect.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.eventconnect.ui.auth.*
import com.example.eventconnect.ui.home.*
import com.example.eventconnect.ui.admin.AdminNgoReviewScreen
import com.example.eventconnect.ui.menu.*
import com.example.eventconnect.ui.booking.*
import com.example.eventconnect.ui.ngo.*
import com.example.eventconnect.ui.profile.*
import com.example.eventconnect.ui.chat.ChatScreen
import com.example.eventconnect.ui.revenue.RevenueScreen
import com.example.eventconnect.ui.payment.PaymentHistoryScreen
import com.example.eventconnect.ui.preparation.*
import com.example.eventconnect.ui.surplus.*
import com.google.firebase.auth.FirebaseAuth

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {

    object OrganizerHome : BottomNavItem("organizer-home", Icons.Default.Home, "Home")
    object OrganizerEvents : BottomNavItem("my-events", Icons.AutoMirrored.Filled.FormatListBulleted, "Events")
    object OrganizerCreateEvent : BottomNavItem("create-event", Icons.Default.AddBox, "Create")
    object OrganizerBookings : BottomNavItem("organizer-bookings", Icons.AutoMirrored.Filled.ReceiptLong, "Bookings")
    object OrganizerProfile : BottomNavItem("organizer-profile", Icons.Default.AccountCircle, "Profile")

    object CatererHome : BottomNavItem("caterer-home", Icons.Default.Home, "Home")
    object CatererBookings : BottomNavItem("caterer-bookings", Icons.AutoMirrored.Filled.List, "Bookings")
    object CatererProfile : BottomNavItem("caterer-profile", Icons.Default.AccountCircle, "Profile")

    object NgoHome : BottomNavItem("ngo-home", Icons.Default.Home, "Home")
    object NgoProfile : BottomNavItem("ngo-profile", Icons.Default.AccountCircle, "Profile")
}

@Composable
fun NavGraph() {

    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val organizerRoutes = listOf(
        BottomNavItem.OrganizerHome.route,
        BottomNavItem.OrganizerEvents.route,
        BottomNavItem.OrganizerCreateEvent.route,
        BottomNavItem.OrganizerBookings.route,
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

                /* -------- ORGANIZER -------- */

                currentRoute in organizerRoutes -> {

                    NavigationBar(
                        containerColor = Color(0xFF9F5FFF)
                    ) {

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

                /* -------- CATERER -------- */

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
                            icon = { Icon(Icons.AutoMirrored.Filled.List, null) },
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
                            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null) },
                            label = { Text("Logout") }
                        )
                    }
                }

                /* -------- NGO -------- */

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

            /* -------- AUTH -------- */

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

            /* -------- ORGANIZER -------- */

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

            composable("organizer-bookings") {
                OrganizerBookingsScreen(navController)
            }

            /* -------- CHAT -------- */

            composable(
                route = "chat/{bookingId}",
                arguments = listOf(
                    navArgument("bookingId") { type = NavType.IntType }
                )
            ) {

                val bookingId = it.arguments?.getInt("bookingId")!!

                ChatScreen(bookingId)
            }

            /* -------- FOOD PREDICTION -------- */

            composable(
                route = "food_prediction/{bookingId}",
                arguments = listOf(
                    navArgument("bookingId") { type = NavType.IntType }
                )
            ) {

                val bookingId = it.arguments?.getInt("bookingId")!!

                FoodPredictionScreen(bookingId)
            }

            /* -------- PREPARATION TRACKING -------- */

            composable(
                route = "preparation/{bookingId}",
                arguments = listOf(
                    navArgument("bookingId") { type = NavType.IntType }
                )
            ) {

                val bookingId = it.arguments?.getInt("bookingId")!!

                PreparationStatusScreen(bookingId)
            }

            composable(
                route = "caterer-preparation/{bookingId}",
                arguments = listOf(
                    navArgument("bookingId") { type = NavType.IntType }
                )
            ) {

                val bookingId = it.arguments?.getInt("bookingId")!!

                CatererPreparationScreen(bookingId)
            }

            /* -------- SURPLUS FOOD -------- */

            composable(
                route = "send-surplus/{eventId}",
                arguments = listOf(
                    navArgument("eventId") { type = NavType.IntType }
                )
            ) {

                val eventId = it.arguments?.getInt("eventId")!!

                SendSurplusScreen(
                    navController = navController,
                    eventId = eventId,
                    latitude = 0.0,
                    longitude = 0.0
                )
            }

            composable("waiting-for-ngo") {
                WaitingForNGOScreen()
            }

            /* -------- CATERER -------- */

            composable(BottomNavItem.CatererHome.route) {
                CatererHomeScreen(navController)
            }

            composable(BottomNavItem.CatererBookings.route) {
                CatererBookingsScreen(navController)
            }

            composable(BottomNavItem.CatererProfile.route) {
                CatererProfileScreen(navController)
            }

            composable("caterer-menu") {
                CatererMenuManagementScreen()
            }

            composable("caterer-revenue") {
                RevenueScreen()
            }

            composable("caterer-payment-history") {
                PaymentHistoryScreen()
            }

            /* -------- NGO -------- */

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

            composable("admin-ngo-review") {
                AdminNgoReviewScreen(navController)
            }
        }
    }
}
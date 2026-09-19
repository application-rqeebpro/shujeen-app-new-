package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.components.ShajeenBottomNavBar
import com.example.ui.components.ShajeenTopAppBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AdminPanelViewModel
import com.example.viewmodel.ShajeenViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ShajeenViewModel by viewModels()
    private val adminViewModel: AdminPanelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ShajeenApp(viewModel = viewModel, adminViewModel = adminViewModel)
            }
        }
    }
}

@Composable
fun ShajeenApp(
    viewModel: ShajeenViewModel,
    adminViewModel: AdminPanelViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "splash"

    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()
    val unreadCount by viewModel.unreadNotificationsCount.collectAsState()

    val bottomNavRoutes = listOf("home", "services", "my_bookings", "notifications", "profile")
    val showBottomBar = currentRoute in bottomNavRoutes
    val showTopBar = currentRoute in bottomNavRoutes

    val topBarTitle = when (currentRoute) {
        "home" -> "وكالة شجين للسفريات والسياحة"
        "services" -> "دليل الخدمات"
        "my_bookings" -> "حجوزاتي"
        "notifications" -> "الإشعارات"
        "profile" -> "الملف الشخصي"
        else -> "شجين للسفريات"
    }

    val topBarSubtitle = when (currentRoute) {
        "home" -> "صنعاء - شارع خولان"
        else -> null
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (showTopBar) {
                ShajeenTopAppBar(
                    title = topBarTitle,
                    subtitle = topBarSubtitle,
                    unreadCount = unreadCount,
                    onNotificationsClick = {
                        if (currentRoute != "notifications") {
                            navController.navigate("notifications")
                        }
                    },
                    isAdminUser = isAdmin,
                    onAdminClick = {
                        navController.navigate("admin_dashboard")
                    }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                ShajeenBottomNavBar(
                    currentRoute = currentRoute,
                    unreadNotifications = unreadCount,
                    onNavigate = { targetRoute ->
                        if (currentRoute != targetRoute) {
                            navController.navigate(targetRoute) {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            // Splash Screen
            composable("splash") {
                SplashScreen(
                    isLoggedIn = isLoggedIn,
                    onNavigateNext = { loggedIn ->
                        val target = if (loggedIn) "home" else "login"
                        navController.navigate(target) {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                )
            }

            // Login Screen
            composable("login") {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate("register")
                    },
                    onNavigateToAdmin = {
                        navController.navigate("admin_login")
                    }
                )
            }

            // Register Screen
            composable("register") {
                RegisterScreen(
                    viewModel = viewModel,
                    onRegisterSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Home Screen
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToBookingWizard = {
                        navController.navigate("booking_wizard")
                    },
                    onNavigateToMyBookings = {
                        navController.navigate("my_bookings")
                    },
                    onNavigateToServices = {
                        navController.navigate("services")
                    },
                    onBookingClick = { bookingId ->
                        navController.navigate("booking_detail/$bookingId")
                    }
                )
            }

            // Services Screen
            composable("services") {
                ServicesScreen(
                    viewModel = viewModel,
                    onBookServiceClick = {
                        viewModel.startNewBooking()
                        navController.navigate("booking_wizard")
                    }
                )
            }

            // Booking Wizard Screen
            composable("booking_wizard") {
                BookingWizardScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onBookingCreated = { booking ->
                        navController.navigate("booking_success/${booking.id}") {
                            popUpTo("booking_wizard") { inclusive = true }
                        }
                    }
                )
            }

            // Booking Success Screen
            composable(
                route = "booking_success/{bookingId}",
                arguments = listOf(navArgument("bookingId") { type = NavType.LongType })
            ) { backStackEntry ->
                val bookingId = backStackEntry.arguments?.getLong("bookingId") ?: 0L
                val lastBooking = viewModel.lastSubmittedBooking.collectAsState().value

                if (lastBooking != null) {
                    BookingSuccessScreen(
                        booking = lastBooking,
                        viewModel = viewModel,
                        onViewBooking = { id ->
                            navController.navigate("booking_detail/$id") {
                                popUpTo("home")
                            }
                        },
                        onNavigateHome = {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    )
                } else {
                    // Fallback to home
                    navController.navigate("home")
                }
            }

            // My Bookings Screen
            composable("my_bookings") {
                MyBookingsScreen(
                    viewModel = viewModel,
                    onBookingClick = { bookingId ->
                        navController.navigate("booking_detail/$bookingId")
                    },
                    onStartNewBooking = {
                        viewModel.startNewBooking()
                        navController.navigate("booking_wizard")
                    }
                )
            }

            // Booking Detail Screen
            composable(
                route = "booking_detail/{bookingId}",
                arguments = listOf(navArgument("bookingId") { type = NavType.LongType })
            ) { backStackEntry ->
                val bookingId = backStackEntry.arguments?.getLong("bookingId") ?: 0L
                BookingDetailScreen(
                    bookingId = bookingId,
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Notifications Screen
            composable("notifications") {
                NotificationsScreen(
                    viewModel = viewModel,
                    onBookingClick = { bookingId ->
                        navController.navigate("booking_detail/$bookingId")
                    }
                )
            }

            // Profile Screen
            composable("profile") {
                ProfileScreen(
                    viewModel = viewModel,
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToAdmin = {
                        navController.navigate("admin_dashboard")
                    }
                )
            }

            // Admin Login Screen (Protected by '77777')
            composable("admin_login") {
                AdminLoginScreen(
                    viewModel = adminViewModel,
                    onLoginSuccess = {
                        navController.navigate("admin_dashboard") {
                            popUpTo("admin_login") { inclusive = true }
                        }
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Admin Dashboard Screen (with functional cards for Add Service, Send Notification, Change Admin Password)
            composable("admin_dashboard") {
                AdminDashboardScreen(
                    viewModel = adminViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onOpenFullAdmin = {
                        navController.navigate("admin")
                    },
                    onBookingClick = { bookingId ->
                        navController.navigate("booking_detail/$bookingId")
                    }
                )
            }

            // Full Multi-Tab Admin Screen
            composable("admin") {
                AdminScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onBookingClick = { bookingId ->
                        navController.navigate("booking_detail/$bookingId")
                    }
                )
            }
        }
    }
}

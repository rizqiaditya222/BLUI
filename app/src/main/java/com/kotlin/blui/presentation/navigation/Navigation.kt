package com.kotlin.blui.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kotlin.blui.data.api.TokenManager
import com.kotlin.blui.presentation.auth.login.LoginScreen
import com.kotlin.blui.presentation.auth.register.RegisterScreen
import com.kotlin.blui.presentation.category.AddCategory
import com.kotlin.blui.presentation.detail.DetailScreen
import com.kotlin.blui.presentation.main.MainScreen
import com.kotlin.blui.presentation.transaction.TransactionScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
    object Transaction : Screen("transaction/{mode}?transactionId={transactionId}") {
        fun createRoute(mode: String, transactionId: String? = null) =
            if (transactionId != null) "transaction/$mode?transactionId=$transactionId"
            else "transaction/$mode"
    }
    object Detail : Screen("detail")
    object AddCategory : Screen("add_category")
}

@Composable
fun NavigationGraph(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    // Check if user is already logged in
    val startDestination = if (tokenManager.isLoggedIn()) {
        Screen.Main.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Login Screen
        composable(route = Screen.Login.route) {
            LoginScreen(
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // Register Screen
        composable(route = Screen.Register.route) {
            RegisterScreen(
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // Main Screen (with BottomNav: Home, Profile)
        composable(route = Screen.Main.route) {
            MainScreen(
                onNavigateToTransaction = { mode ->
                    navController.navigate(Screen.Transaction.createRoute(mode))
                },
                onNavigateToDetail = {
                    navController.navigate(Screen.Detail.route)
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Detail Screen
        composable(route = Screen.Detail.route) {
            val savedMonth = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("selected_month")
            val savedYear = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("selected_year")

            DetailScreen(
                onBack = {
                    navController.popBackStack()
                },
                onTransactionClick = { transactionId ->
                    navController.navigate(Screen.Transaction.createRoute("edit", transactionId))
                },
                initialMonth = savedMonth,
                initialYear = savedYear
            )
        }

        // Transaction Screen (Add or Edit)
        composable(
            route = Screen.Transaction.route,
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType },
                navArgument("transactionId") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "add"
            val transactionId = backStackEntry.arguments?.getString("transactionId")?.takeIf { it.isNotEmpty() }
            val isEditMode = mode == "edit"

            TransactionScreen(
                isEditMode = isEditMode,
                transactionId = transactionId,
                initialTransactionType = "Expense",
                onBack = {
                    navController.popBackStack()
                },
                onSave = {
                    println("Save transaction")
                    if (isEditMode) {
                        // Edit mode: back to detail
                        navController.popBackStack()
                    } else {
                        // Add mode: back to home (main screen)
                        navController.popBackStack(Screen.Main.route, inclusive = false)
                    }
                },
                onDelete = { month, year ->
                    println("Delete transaction - month=$month year=$year")
                    // Save month and year to be used when navigating back to Detail
                    navController.currentBackStackEntry?.savedStateHandle?.apply {
                        month?.let { set("selected_month", it) }
                        year?.let { set("selected_year", it) }
                    }
                    // After delete, back to detail screen
                    navController.popBackStack()
                },
                onAddCategory = {
                    navController.navigate(Screen.AddCategory.route)
                }
            )
        }

        // Add Category Screen
        composable(route = Screen.AddCategory.route) {
            AddCategory(
                onDismiss = {
                    navController.popBackStack()
                },
                onCategorySelected = { template, color ->
                    println("Save category: ${template.name}")
                    navController.popBackStack()
                }
            )
        }
    }
}
package com.kotlin.blui.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
    object Transaction : Screen("transaction/{mode}") {
        fun createRoute(mode: String) = "transaction/$mode"
    }
    object Detail : Screen("detail")
    object AddCategory : Screen("add_category")
}

@Composable
fun NavigationGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Login Screen
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginClick = { email, password ->
                    // Handle login
                    println("Login: email=$email")
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // Register Screen
        composable(route = Screen.Register.route) {
            RegisterScreen(
                onRegisterClick = { name, email, dateOfBirth, password ->
                    // Handle register
                    println("Register: name=$name, email=$email, dateOfBirth=$dateOfBirth")
                    // Navigate to login after registration
                    navController.popBackStack()
                },
                onLoginClick = {
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
                }
            )
        }

        // Detail Screen
        composable(route = Screen.Detail.route) {
            DetailScreen(
                onBack = {
                    navController.popBackStack()
                },
                onTransactionClick = {
                    navController.navigate(Screen.Transaction.createRoute("edit"))
                }
            )
        }

        // Transaction Screen (Add or Edit)
        composable(
            route = Screen.Transaction.route,
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "add"
            val isEditMode = mode == "edit"

            TransactionScreen(
                isEditMode = isEditMode,
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
                onDelete = {
                    println("Delete transaction")
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
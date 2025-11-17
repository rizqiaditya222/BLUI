package com.kotlin.blui.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kotlin.blui.presentation.auth.login.LoginScreen
import com.kotlin.blui.presentation.auth.register.RegisterScreen

// Sealed class untuk routes
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
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
                    // TODO: Navigate to home after successful login
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
                    // TODO: Navigate to home after successful registration
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
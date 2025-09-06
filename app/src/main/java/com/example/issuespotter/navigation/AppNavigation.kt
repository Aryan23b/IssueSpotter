package com.example.issuespotter.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.issuespotter.auth.AuthViewModel
import com.example.issuespotter.auth.LoginScreen
import com.example.issuespotter.auth.SignUpScreen
import com.example.issuespotter.screens.HomeScreen
import com.example.issuespotter.screens.ProfileScreen
import com.example.issuespotter.screens.ReportNewIssueScreen
import com.example.issuespotter.screens.user


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController, authViewModel)
        }
        composable("signup") {
            SignUpScreen(navController, authViewModel)
        }
        composable("home") {
            HomeScreen(navController, authViewModel)
        }
        composable("reportNewIssue") {
            ReportNewIssueScreen(navController, authViewModel)

        }
        composable("profile"){
            ProfileScreen(navController, authViewModel, user = user(name = "aryan"))
        }

        composable("rating"){

        }
    }
}
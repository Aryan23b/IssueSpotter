package com.example.issuespotter.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType 
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.issuespotter.auth.AuthViewModel
import com.example.issuespotter.auth.LoginScreen
import com.example.issuespotter.auth.SignUpScreen
import com.example.issuespotter.auth.AuthState
import com.example.issuespotter.screens.HomeScreen
import com.example.issuespotter.screens.IssueDetailScreen
import com.example.issuespotter.screens.ProfileScreen
import com.example.issuespotter.screens.ReportNewIssueScreen
import com.example.issuespotter.screens.UserReportsScreen

@Composable
fun AppNavigation() {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()
    val navController = rememberNavController()

    var showSplashScreen by remember { mutableStateOf(true) }

    if (showSplashScreen) {
        SplashScreen(onTimeout = { showSplashScreen = false })
    } else {
        when (authState) {
            is AuthState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is AuthState.Authenticated -> {
                NavHost(navController = navController, startDestination = "home") {
                    composable("login") { LoginScreen(navController, authViewModel) }
                    composable("signup") { SignUpScreen(navController, authViewModel) }
                    composable("home") { HomeScreen(navController, authViewModel) }
                    composable("reportNewIssue") { ReportNewIssueScreen(navController, authViewModel) }
                    composable("profile") { ProfileScreen(navController, authViewModel) }
                    composable("userReports") { UserReportsScreen(navController, authViewModel) }
                    //composable("rating") { RatingScreen(navController, authViewModel) }
                    composable(
                        route = "issueDetail/{reportId}",
                        arguments = listOf(navArgument("reportId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        IssueDetailScreen(
                            navController = navController,
                            authViewModel = authViewModel,
                            reportId = backStackEntry.arguments?.getString("reportId")
                        )
                    }
                }
            }
            is AuthState.NotAuthenticated, is AuthState.Error -> {
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { LoginScreen(navController, authViewModel) }
                    composable("signup") { SignUpScreen(navController, authViewModel) }
                    // Allowing access to other screens even if not authenticated, as per original structure
                    composable("home") { HomeScreen(navController, authViewModel) } 
                    composable("reportNewIssue") { ReportNewIssueScreen(navController, authViewModel) }
                    composable("profile") { ProfileScreen(navController, authViewModel) }
                    composable("userReports") { UserReportsScreen(navController, authViewModel) }
                   // composable("rating") { RatingScreen(navController, authViewModel) }
                    composable(
                        route = "issueDetail/{reportId}",
                        arguments = listOf(navArgument("reportId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        IssueDetailScreen(
                            navController = navController,
                            authViewModel = authViewModel,
                            reportId = backStackEntry.arguments?.getString("reportId")
                        )
                    }
                }
            }
        }
    }
}
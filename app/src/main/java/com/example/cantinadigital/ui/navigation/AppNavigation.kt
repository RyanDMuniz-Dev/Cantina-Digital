package com.example.cantinadigital.ui.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cantinadigital.data.remote.SupabaseClientProvider
import com.example.cantinadigital.ui.features.home.HomeScreen
import com.example.cantinadigital.ui.features.login.LoginScreen
import com.example.cantinadigital.ui.features.signup.SignUpScreen
import io.github.jan.supabase.auth.auth

sealed class Screen(
    val route: String
) {
    object SignUp : Screen("signup")
    object Login : Screen("login")
    object Home : Screen("home")
}

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    var startDestination by remember { mutableStateOf(Screen.SignUp.route) }
    var isCheckingSection by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val session = SupabaseClientProvider.client.auth.currentUserOrNull()
        startDestination = if (session != null) Screen.Home.route else Screen.SignUp.route
        isCheckingSection = false
    }

    if (isCheckingSection) return

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { it })
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -it })
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -it })
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it })
        }
    ) {
        composable(Screen.SignUp.route) {
            SignUpScreen(
                viewModel = viewModel(),
                onSignUpSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }

            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = viewModel(),
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen()
        }

    }

}
package com.example.cantinadigital.ui.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cantinadigital.codigoTelaPerfil.Tela
import com.example.cantinadigital.data.remote.FirebaseProvider
import com.example.cantinadigital.ui.features.home.HomeScreen
import com.example.cantinadigital.ui.features.login.LoginScreen
import com.example.cantinadigital.ui.features.signup.SignUpScreen
import com.example.cantinadigital.ui.theme.CantinaDigitalTheme

sealed class Screen(
    val route: String
) {
    object SignUp : Screen("signup")
    object Login : Screen("login")
    object Home : Screen("home")

    object Perfil: Screen("perfil")
}

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    var startDestination by remember { mutableStateOf(Screen.SignUp.route) }
    var isCheckingSection by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Verifica se há um usuário logado no Firebase Auth
        val currentUser = FirebaseProvider.auth.currentUser
        startDestination = if (currentUser != null) Screen.Perfil.route else Screen.SignUp.route
        isCheckingSection = false
    }

    if (isCheckingSection) return

    CantinaDigitalTheme(darkTheme = darkMode) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
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

                composable(Screen.Perfil.route) {
                    Tela(darkMode, { darkMode = it })
                }
            }
        }
    }
}
package com.example.cantinadigital.ui.mainScreen

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cantinadigital.ui.components.appbar.CantinaBottomBar
import com.example.cantinadigital.ui.components.appbar.CantinaTopBar
import com.example.cantinadigital.ui.components.appbar.NavigationItem
import com.example.cantinadigital.ui.features.dashboard.DashboardScreen
import com.example.cantinadigital.ui.features.insights.InsightsScreen
import com.example.cantinadigital.ui.features.logs.LogsScreen
import com.example.cantinadigital.ui.features.orders.OrdersScreen
import com.example.cantinadigital.ui.features.stock.StockScreen

const val ROUTE_LOGS = "logs"

@Composable
fun MainScreen(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    // Tenta encontrar o item selecionado. Se for a rota "logs", será null.
    val currentItem = NavigationItem.items.find { it.route == currentRoute }

    Scaffold(
        modifier = modifier,
        topBar = {
            CantinaTopBar(
                modifier = Modifier,
                onNotificationClick = {
                    if (currentRoute != ROUTE_LOGS) {
                        navController.navigate(ROUTE_LOGS) {
                            launchSingleTop = true
                        }
                    }
                },
                onProfileClick = {

                }
            )
        },
        bottomBar = {
            // Exibe a BottomBar apenas se o item atual fizer parte do menu principal
            if (currentItem != null) {
                CantinaBottomBar(
                    modifier = Modifier,
                    selected = currentItem,
                    onSelected = { item ->
                        navController.navigate(item.route) {
                            // Volta até a rota inicial da navegação para não acumular pilhas
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = NavigationItem.Dashboard.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            // Animação para sair da tela atual
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            },
            // Animação ao pressionar "Voltar"
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {

            composable(NavigationItem.Dashboard.route) {
                DashboardScreen()
            }

            composable(NavigationItem.Orders.route) {
                OrdersScreen()
            }

            composable(NavigationItem.Stock.route) {
                StockScreen()
            }

            composable(NavigationItem.Insights.route) {
                InsightsScreen()
            }

            composable(ROUTE_LOGS) {
                LogsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

        }

    }

}

//@Preview
//@Composable
//private fun MainScreenPreview() {
//    CantinaDigitalTheme {
//        MainScreen()
//    }
//}
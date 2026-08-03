package com.example.cantinadigital.ui.mainScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cantinadigital.ui.components.appbar.CantinaBottomBar
import com.example.cantinadigital.ui.components.appbar.CantinaTopBar
import com.example.cantinadigital.ui.components.appbar.NavigationItem
import com.example.cantinadigital.ui.features.dashboard.DashboardScreen
import com.example.cantinadigital.ui.features.insights.InsightsScreen
import com.example.cantinadigital.ui.features.orders.OrdersScreen
import com.example.cantinadigital.ui.features.stock.StockScreen
import com.example.cantinadigital.ui.features.stock.StockViewModel
import com.example.cantinadigital.ui.theme.CantinaDigitalTheme

@Composable
fun MainScreen(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    val currentItem = NavigationItem.items.find {
        it.route == currentRoute
    } ?: NavigationItem.Dashboard

    Scaffold(
        modifier = modifier,
        topBar = {
            CantinaTopBar(
                modifier = Modifier,
                onNotificationClick = {

                },
                onProfileClick = {

                }
            )
        },
        bottomBar = {
            CantinaBottomBar(
                modifier = Modifier,
                selected = currentItem,
                onSelected = { item ->

                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                    }

                }
            )
        }
    ) {innerPadding ->

        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = NavigationItem.Dashboard.route,
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

        }

    }

}

@Preview
@Composable
private fun MainScreenPreview() {
    CantinaDigitalTheme {
        MainScreen()
    }
}
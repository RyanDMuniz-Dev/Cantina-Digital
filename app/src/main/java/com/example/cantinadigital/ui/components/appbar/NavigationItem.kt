package com.example.cantinadigital.ui.components.appbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.SpaceDashboard
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem (
    val title: String,
    val route: String,
    val icon: ImageVector
) {
    object Dashboard: NavigationItem(
        title = "Dashboard",
        route = "dashboard",
        Icons.Outlined.SpaceDashboard
    )
    object Orders: NavigationItem(
        title = "Pedidos",
        route = "orders",
        Icons.AutoMirrored.Outlined.ReceiptLong
    )
    object Stock: NavigationItem(
        title = "Estoque",
        route = "stock",
        Icons.Outlined.Inventory2
    )
    object Insights: NavigationItem(
        title = "Insights",
        route = "insights",
        Icons.Outlined.Analytics
    )

    companion object {
        val items = listOf(
            Dashboard,
            Orders,
            Stock,
            Insights
        )
    }
}
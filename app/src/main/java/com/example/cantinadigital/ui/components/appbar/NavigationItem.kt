package com.example.cantinadigital.ui.components.appbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.SpaceDashboard
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem (
    val title: String,
    val icon: ImageVector
) {
    object Dashboard: NavigationItem(
        title = "Dashboard",
        Icons.Outlined.SpaceDashboard
    )
    object Orders: NavigationItem(
        title = "Pedidos",
        Icons.AutoMirrored.Outlined.ReceiptLong
    )
    object Stock: NavigationItem(
        title = "Estoque",
        Icons.Outlined.Inventory2
    )
    object Insights: NavigationItem(
        title = "Insights",
        Icons.Outlined.Analytics
    )
}
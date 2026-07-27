package com.example.cantinadigital.ui.components.appbar

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.cantinadigital.ui.theme.CantinaDigitalTheme

@Composable
fun CantinaBottomBar(
    modifier: Modifier = Modifier,
    selected: NavigationItem,
    onSelected: (NavigationItem) -> Unit
) {

    val items = listOf(
        NavigationItem.Dashboard,
        NavigationItem.Orders,
        NavigationItem.Stock,
        NavigationItem.Insights
    )

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = selected == item,
                onClick = {
                    onSelected(item)
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(text = item.title)
                }
            )
        }
    }

}

@Preview
@Composable
private fun CantinaBottomBarPreview() {
    CantinaDigitalTheme(
        darkTheme = false
    ) {
        CantinaBottomBar(
            selected = NavigationItem.Orders
        ) {
            
        }
    }
}
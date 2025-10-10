package com.tekome.downloadimages.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.tekome.feature.downloads.navigation.DOWNLOADS_ROUTE
import com.tekome.feature.images.navigation.IMAGES_ROUTE

data class TabModel(
    val title: String,
    val route: String,
    val iconRes: ImageVector,
)

val tabs =
    mutableListOf(
        TabModel(
            title = "Images",
            route = IMAGES_ROUTE,
            iconRes = Icons.Filled.Image,
        ),
        TabModel(
            title = "Downloads",
            route = DOWNLOADS_ROUTE,
            iconRes = Icons.Filled.Download,
        ),
    )

@Composable
fun RowScope.BottomNavigationItems(
    items: List<TabModel>,
    currentDestination: NavDestination?,
    bottomBarNavController: NavHostController,
) {
    items.forEachIndexed { _, screen ->
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = screen.iconRes,
                    contentDescription = null,
                )
            },
            label = {
                Text(screen.title, maxLines = 1)
            },
            selected =
                currentDestination?.hierarchy?.any {
                    it.route?.startsWith(screen.route) == true
                } == true,
            onClick = {
                bottomBarNavController.navigate(screen.route) {
                    popUpTo(bottomBarNavController.graph.findStartDestination().id) {
                        saveState = true
                    }

                    launchSingleTop = true
                    restoreState = true
                }
            },
        )
    }
}

package com.tekome.feature.downloads.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.tekome.feature.downloads.DownloadsRoute

const val DOWNLOADS_ROUTE = "downloads"

fun NavController.navigateToDownloads(navOptions: NavOptions) = navigate(route = DOWNLOADS_ROUTE, navOptions = navOptions)

fun NavGraphBuilder.downloadsScreen() {
    composable(DOWNLOADS_ROUTE) {
        DownloadsRoute()
    }
}

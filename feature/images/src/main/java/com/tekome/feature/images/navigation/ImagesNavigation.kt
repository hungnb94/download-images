package com.tekome.feature.images.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.tekome.feature.images.ImagesRoute

const val IMAGES_ROUTE = "images"

fun NavController.navigateToImages(navOptions: NavOptions) = navigate(route = IMAGES_ROUTE, navOptions = navOptions)

fun NavGraphBuilder.imagesScreen(onNavigateToDetail: (String) -> Unit = {}) {
    composable(IMAGES_ROUTE) {
        ImagesRoute(onNavigateToDetail = onNavigateToDetail)
    }
}

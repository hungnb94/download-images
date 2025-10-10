package com.tekome.downloadimages

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tekome.downloadimages.navigation.BottomNavigationItems
import com.tekome.downloadimages.navigation.tabs
import com.tekome.downloadimages.ui.theme.DownloadImagesTheme
import com.tekome.feature.downloads.navigation.downloadsScreen
import com.tekome.feature.images.navigation.IMAGES_ROUTE
import com.tekome.feature.images.navigation.imagesScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DownloadImagesTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Scaffold(
                        bottomBar =
                            {
                                NavigationBar {
                                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                                    val currentDestination = navBackStackEntry?.destination

                                    BottomNavigationItems(
                                        items = tabs,
                                        currentDestination = currentDestination,
                                        bottomBarNavController = navController,
                                    )
                                }
                            },
                    ) { innerPadding ->
                        NavHost(
                            modifier = Modifier.padding(innerPadding),
                            navController = navController,
                            startDestination = IMAGES_ROUTE,
                        ) {
                            imagesScreen()
                            downloadsScreen()
                        }
                    }
                }
            }
        }
    }
}

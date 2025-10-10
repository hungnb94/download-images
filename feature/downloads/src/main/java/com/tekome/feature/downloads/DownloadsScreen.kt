package com.tekome.feature.downloads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

@Composable
fun DownloadsRoute(
    viewModel: DownloadsViewModel = hiltViewModel(LocalViewModelStoreOwner.current!!, null),
) {
    DownloadsScreen()
}

@Composable
fun DownloadsScreen(modifier: Modifier = Modifier) {
}

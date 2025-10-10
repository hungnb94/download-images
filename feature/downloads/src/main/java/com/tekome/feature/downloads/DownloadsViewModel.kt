package com.tekome.feature.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tekome.core.data.downloader.ImageDownloadManager
import com.tekome.core.model.DownloadTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DownloadsViewModel
    @Inject
    constructor(
        downloadManager: ImageDownloadManager,
    ) : ViewModel() {
        val uiState: StateFlow<DownloadsUiState> =
            downloadManager
                .getDownloadedImages()
                .map { downloadTasks -> DownloadsUiState(isLoading = false, downloads = downloadTasks) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = DownloadsUiState(isLoading = true, downloads = listOf()),
                )
    }

data class DownloadsUiState(
    val isLoading: Boolean = true,
    val downloads: List<DownloadTask> = emptyList(),
)

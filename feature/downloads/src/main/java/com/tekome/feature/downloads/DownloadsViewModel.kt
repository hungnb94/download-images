package com.tekome.feature.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tekome.core.common.Dispatcher
import com.tekome.core.common.NiaDispatchers
import com.tekome.core.data.downloader.ImageDownloadManager
import com.tekome.core.model.DownloadTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DownloadsViewModel
    @Inject
    constructor(
        downloadManagerProvider: dagger.Lazy<ImageDownloadManager>,
        @Dispatcher(NiaDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    ) : ViewModel() {
        init {
            Timber.i("Init DownloadsViewModel")
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        val uiState: StateFlow<DownloadsUiState> =
            flow {
                Timber.i("Thread: ${Thread.currentThread().name}")
                emit(downloadManagerProvider.get())
            }.flowOn(ioDispatcher)
                .flatMapLatest { downloadManager -> downloadManager.getDownloadedImages() }
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

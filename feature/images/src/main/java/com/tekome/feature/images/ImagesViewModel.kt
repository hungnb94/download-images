package com.tekome.feature.images

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tekome.core.data.downloader.ImageDownloadManager
import com.tekome.core.data.repository.ImageRepository
import com.tekome.core.model.DownloadTask
import com.tekome.core.model.Image
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ImagesViewModel
    @Inject
    constructor(
        imageRepository: dagger.Lazy<ImageRepository>,
        private val downloadManager: dagger.Lazy<ImageDownloadManager>,
    ) : ViewModel() {
        private val _selectedImageIds: MutableStateFlow<Set<String>> = MutableStateFlow(setOf())
        val selectedImageIds: StateFlow<Set<String>> = _selectedImageIds.asStateFlow()

        val downloadTasks: StateFlow<List<DownloadTask>> =
            downloadManager
                .get()
                .getDownloadedImages()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = listOf(),
                )

        val imagesUiState: StateFlow<ImagesUiState> =
            imageRepository
                .get()
                .getImages()
                .map<List<Image>, ImagesUiState> { images -> ImagesUiState.Success(images) }
                .onStart {
                    emit(ImagesUiState.Loading)
                }.catch { throwable ->
                    Timber.e(throwable, "Get images failed")
                    emit(ImagesUiState.Error(throwable.message ?: "Unknown error"))
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = ImagesUiState.Loading,
                )

        fun onImageClick(imageId: String) {
            val currentSelected = _selectedImageIds.value.toMutableSet()
            if (currentSelected.contains(imageId)) {
                currentSelected.remove(imageId)
            } else {
                currentSelected.add(imageId)
            }
            _selectedImageIds.value = currentSelected
        }

        fun onSelectAll() {
            _selectedImageIds.value =
                (imagesUiState.value as ImagesUiState.Success).images.map { it.id }.toSet()
        }

        fun onDeselectAll() {
            _selectedImageIds.value = setOf()
        }

        fun downloadSelectedImages() {
            val selectedIds = _selectedImageIds.value
            if (selectedIds.isEmpty()) {
                Timber.w("Download clicked but no images are selected.")
                return
            }

            Timber.d("Starting download for ${selectedIds.size} images")
            val images = (imagesUiState.value as ImagesUiState.Success).images
            selectedIds.forEach { id ->
                val image =
                    images
                        .find { it.id == id }
                if (image != null) {
                    val fileName = image.id + "_" + image.url.substringAfterLast("/")
                    downloadManager.get().downloadImage(
                        url = image.url,
                        fileName = fileName,
                    )
                }
            }
        }
    }

sealed interface ImagesUiState {
    data object Loading : ImagesUiState

    data class Success(
        val images: List<Image>,
    ) : ImagesUiState

    data class Error(
        val message: String,
    ) : ImagesUiState
}

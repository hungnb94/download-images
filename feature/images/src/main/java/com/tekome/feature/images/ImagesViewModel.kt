package com.tekome.feature.images

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tekome.core.data.repository.ImageRepository
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
        imageRepository: ImageRepository,
    ) : ViewModel() {
        val imagesUiState: StateFlow<ImagesUiState> =
            imageRepository
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

        private val _selectedImageIds: MutableStateFlow<Set<String>> = MutableStateFlow(setOf())
        val selectedImageIds: StateFlow<Set<String>> = _selectedImageIds.asStateFlow()

        fun onImageClick(imageId: String) {
            val currentSelected = _selectedImageIds.value.toMutableSet()
            if (currentSelected.contains(imageId)) {
                currentSelected.remove(imageId)
            } else {
                currentSelected.add(imageId)
            }
            _selectedImageIds.value = currentSelected
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

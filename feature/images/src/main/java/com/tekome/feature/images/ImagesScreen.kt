package com.tekome.feature.images

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tekome.core.model.Image

@Composable
fun ImagesRoute(viewModel: ImagesViewModel = hiltViewModel()) {
    val uiState by viewModel.imagesUiState.collectAsStateWithLifecycle()
    ImagesScreen(
        uiState = uiState,
    )
}

@Composable
fun ImagesScreen(uiState: ImagesUiState) {
    Scaffold(
        topBar = {
            SelectAllTopBar(
                selectedCount = 0,
                totalCount = if (uiState is ImagesUiState.Success) uiState.images.size else 0,
                onEvent = {},
            )
        },
    ) { paddingValues ->
        when (uiState) {
            is ImagesUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is ImagesUiState.Success -> {
                ImageGrid(
                    modifier = Modifier.padding(paddingValues),
                    images = uiState.images,
                    selectedImageIds = setOf(),
                    onImageClick = { imageId ->
//                        viewModel.handleEvent(ImagePickerEvent.ImageClicked(imageId))
                    },
                )
            }

            is ImagesUiState.Error -> {
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectAllTopBar(
    selectedCount: Int,
    totalCount: Int,
    onEvent: (Image) -> Unit,
) {
    val allSelected = selectedCount > 0 && selectedCount == totalCount

    TopAppBar(
        title = { Text(text = if (selectedCount > 0) "Đã chọn $selectedCount" else stringResource(R.string.select)) },
        actions = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text =
                        if (allSelected) {
                            stringResource(R.string.unselect_all)
                        } else {
                            stringResource(R.string.select_all)
                        },
                    style = MaterialTheme.typography.bodyLarge,
                )
                Checkbox(
                    checked = allSelected,
                    onCheckedChange = { isChecked ->
//                        val event =
//                            if (isChecked) {
//                                ImagePickerEvent.SelectAllClicked
//                            } else {
//                                ImagePickerEvent.DeselectAllClicked
//                            }
//                        onEvent(event)
                    },
                )
            }
        },
    )
}

@Composable
private fun ImageGrid(
    modifier: Modifier = Modifier,
    images: List<Image>,
    selectedImageIds: Set<String>,
    onImageClick: (String) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(4.dp),
    ) {
        items(images, key = { it.id }) { image ->
            ImageItemView(
                image = image,
                isSelected = selectedImageIds.contains(image.id),
                onClick = { onImageClick(image.id) },
            )
        }
    }
}

@Composable
private fun ImageItemView(
    image: Image,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .padding(4.dp)
                .aspectRatio(1f)
                .clip(MaterialTheme.shapes.medium)
                .clickable(onClick = onClick)
                .then(
                    if (isSelected) {
                        Modifier.border(
                            3.dp,
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.shapes.medium,
                        )
                    } else {
                        Modifier
                    },
                ),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = image.url,
            contentDescription = "Image ${image.id}",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Text(text = image.id, style = MaterialTheme.typography.bodyLarge)

        if (isSelected) {
            Icon(
                painter = painterResource(id = android.R.drawable.checkbox_on_background),
                contentDescription = "Selected",
                tint = Color.White,
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp),
            )
        }
    }
}

package com.tekome.feature.images

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.tekome.core.model.DownloadTask
import com.tekome.core.model.Image
import timber.log.Timber

@Composable
fun ImagesRoute(
    viewModel: ImagesViewModel =
        hiltViewModel(LocalViewModelStoreOwner.current!!, null),
) {
    val uiState by viewModel.imagesUiState.collectAsStateWithLifecycle()
    val selectedImageIds by viewModel.selectedImageIds.collectAsStateWithLifecycle()
    val downloadTasks by viewModel.downloadTasks.collectAsStateWithLifecycle()
    Timber.d("Selected images: $selectedImageIds")

    ImagesScreen(
        uiState = uiState,
        selectedImageIds = selectedImageIds,
        onImageClick = viewModel::onImageClick,
        onSelectAll = { all ->
            if (all) {
                viewModel.onSelectAll()
            } else {
                viewModel.onDeselectAll()
            }
        },
        onDownload = viewModel::downloadSelectedImages,
    )
}

@Composable
fun ImagesScreen(
    uiState: ImagesUiState,
    selectedImageIds: Set<String>,
    onImageClick: (String) -> Unit = {},
    onSelectAll: (Boolean) -> Unit = {},
    onDownload: () -> Unit = {},
    downloadTasks: List<DownloadTask> = listOf(),
) {
    Scaffold(
        topBar = {
            SelectAllTopBar(
                selectedCount = selectedImageIds.size,
                totalCount = if (uiState is ImagesUiState.Success) uiState.images.size else 0,
                onSelectAll = onSelectAll,
                downloaded = downloadTasks.count { it.isFinished && it.progress == 100 },
                totalDownload = downloadTasks.size,
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = selectedImageIds.isNotEmpty(),
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {
                BottomAppBar {
                    DownloadBottomBar(
                        selectedCount = selectedImageIds.size,
                        onDownloadClick = onDownload,
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize(),
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
                    selectedImageIds = selectedImageIds,
                    onImageClick = onImageClick,
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
    selectedCount: Int = 0,
    totalCount: Int = 0,
    downloaded: Int = 0,
    totalDownload: Int = 0,
    onSelectAll: (Boolean) -> Unit = {},
) {
    val allSelected = selectedCount > 0 && selectedCount == totalCount

    TopAppBar(
        title = {
            Text(
                text =
                    if (selectedCount > 0) {
                        stringResource(R.string.selected_count, selectedCount)
                    } else {
                        stringResource(R.string.select)
                    },
            )
        },
        actions = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (totalDownload > 0) {
                    Text(
                        text = "Download $downloaded/$totalDownload",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Spacer(Modifier.width(8.dp))
                }
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
                    onCheckedChange = onSelectAll,
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
    Column {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
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
}

@OptIn(ExperimentalGlideComposeApi::class)
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
                            4.dp,
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.shapes.medium,
                        )
                    } else {
                        Modifier
                    },
                ),
        contentAlignment = Alignment.Center,
    ) {
        GlideImage(
            model = image.url,
            contentDescription = "Image ${image.id}",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        Text(
            text = image.id,
            style =
                MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                ),
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Outlined.Done,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-8).dp, y = (-8).dp)
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.onPrimary,
                            MaterialTheme.shapes.extraSmall,
                        ).padding(4.dp)
                        .size(20.dp),
            )
        }
    }
}

@Composable
private fun DownloadBottomBar(
    selectedCount: Int,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shadowElevation = 8.dp,
    ) {
        Button(
            onClick = onDownloadClick,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .height(50.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "Download Icon",
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(R.string.download_count, selectedCount))
        }
    }
}

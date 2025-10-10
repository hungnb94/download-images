package com.tekome.feature.downloads

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.tekome.core.data.model.isSuccess
import com.tekome.core.designsystem.components.LoadingIndicator
import com.tekome.core.model.DownloadTask

@Composable
fun DownloadsRoute(
    viewModel: DownloadsViewModel =
        hiltViewModel(
            LocalViewModelStoreOwner.current!!,
            null,
        ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DownloadsScreen(uiState = uiState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(uiState: DownloadsUiState) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    stringResource(
                        R.string.downloads_count,
                        uiState.downloads.size,
                    ),
                )
            })
        },
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                LoadingIndicator()
            }

            !uiState.isLoading && uiState.downloads.isEmpty() -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(stringResource(R.string.no_downloads))
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = innerPadding,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    items(
                        items = uiState.downloads,
                        key = { it.id },
                    ) { downloadItem ->
                        DownloadListItem(item = downloadItem)
                    }
                }
            }
        }
    }
}

@Composable
private fun DownloadListItem(item: DownloadTask) {
    val statusIcon: ImageVector
    val iconColor: Color
    val statusText: String

    when {
        !item.isFinished && item.progress > 0 -> {
            statusIcon = Icons.Default.CloudDownload
            iconColor = MaterialTheme.colorScheme.primary
            statusText = "Downloading... ${item.progress}%"
        }

        !item.isFinished -> {
            statusIcon = Icons.Default.HourglassTop
            iconColor = MaterialTheme.colorScheme.onSurfaceVariant
            statusText = "Waiting..."
        }

        !item.isSuccess -> {
            statusIcon = Icons.Default.Error
            iconColor = MaterialTheme.colorScheme.error
            statusText = "Download failed"
        }

        else -> {
            statusIcon = Icons.Default.CheckCircle
            iconColor = Color(0xFF1B5E20)
            statusText = "Download success"
        }
    }

    Card(
        modifier =
            Modifier
                .fillMaxWidth(0.95f),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = statusText,
                    tint = iconColor,
                    modifier = Modifier.size(32.dp),
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.outputUri,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.MiddleEllipsis,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = iconColor,
                    )
                }
            }

            if (!item.isFinished && item.progress > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { item.progress / 100f },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

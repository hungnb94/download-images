package com.tekome.core.data.downloader

import com.tekome.core.model.DownloadTask
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ImageDownloadManager {
    fun downloadImage(
        url: String,
        fileName: String,
    ): UUID

    fun getDownloadedImages(): Flow<List<DownloadTask>>
}

internal const val DOWNLOAD_WORK_NAME = "DownloadWorkName"
internal const val DOWNLOAD_WORK_TAG = "DownloadWorkTag"

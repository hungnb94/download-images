package com.tekome.core.data.model

import com.tekome.core.data.downloader.DownloadWorker
import com.tekome.core.model.DownloadTask

val DownloadTask.isSuccess: Boolean
    get() = progress == DownloadWorker.PROGRESS_SUCCESSFUL

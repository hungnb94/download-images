package com.tekome.core.data.downloader

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.UUID
import javax.inject.Inject

class WorkManagerImageDownloader
    @Inject
    constructor(
        private val workManager: WorkManager,
    ) : ImageDownloader {
        override fun downloadImage(
            url: String,
            fileName: String,
        ): UUID {
            val constraints =
                Constraints
                    .Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

            val inputData =
                workDataOf(
                    DownloadWorker.KEY_INPUT_URL to url,
                    DownloadWorker.KEY_INPUT_FILENAME to fileName,
                )

            val workRequest =
                OneTimeWorkRequestBuilder<DownloadWorker>()
                    .setConstraints(constraints)
                    .setInputData(inputData)
                    .build()

            workManager.enqueue(workRequest)
            return workRequest.id
        }
    }

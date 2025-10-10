package com.tekome.core.data.downloader

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.tekome.core.model.DownloadTask
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class WorkManagerImageDownloadManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : ImageDownloadManager {
        init {
            Timber.i("Init ImageDownloadManager")
        }

        private val workManager = WorkManager.getInstance(context)

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
                    .addTag(DOWNLOAD_WORK_TAG)
                    .build()

            workManager.enqueueUniqueWork(
                DOWNLOAD_WORK_NAME,
                ExistingWorkPolicy.APPEND,
                workRequest,
            )
            return workRequest.id
        }

        override fun getDownloadedImages(): Flow<List<DownloadTask>> =
            workManager
                .getWorkInfosByTagFlow(DOWNLOAD_WORK_TAG)
                .map { workInfos ->
                    workInfos
                        .map { workInfo ->
                            val isFinished = workInfo.state.isFinished
                            val data = if (isFinished) workInfo.outputData else workInfo.progress
                            val inputUrl = data.getString(DownloadWorker.KEY_INPUT_URL) ?: ""
                            val outputUri = data.getString(DownloadWorker.KEY_OUTPUT_URI) ?: ""
                            val progress = data.getInt(DownloadWorker.KEY_PROGRESS, 0)

                            DownloadTask(
                                id = workInfo.id,
                                inputUrl = inputUrl,
                                outputUri = outputUri,
                                progress = progress,
                                isFinished = isFinished,
                            )
                        }
                }
    }

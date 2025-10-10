package com.tekome.core.data.downloader

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.tekome.core.data.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DownloadWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {
    companion object {
        const val KEY_INPUT_URL = "KEY_INPUT_URL"
        const val KEY_INPUT_FILENAME = "KEY_INPUT_FILENAME"
        const val KEY_OUTPUT_URI = "KEY_OUTPUT_URI"
        const val KEY_PROGRESS = "KEY_PROGRESS"
        const val PROGRESS_SUCCESSFUL = 100
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "Download"
    }

    override suspend fun doWork(): Result {
        val imageUrl = inputData.getString(KEY_INPUT_URL) ?: return Result.failure()
        val fileName = inputData.getString(KEY_INPUT_FILENAME) ?: return Result.failure()

//        createNotificationChannel()
//        val notification =
//            createNotification(
//                fileName,
//                applicationContext.getString(R.string.start_downloading),
//            )
//        setForeground(androidx.work.ForegroundInfo(NOTIFICATION_ID, notification))
        val outputFile = File(applicationContext.filesDir, fileName)
        setProgress(
            workDataOf(
                KEY_INPUT_URL to imageUrl,
                KEY_OUTPUT_URI to outputFile.toURI().toString(),
                KEY_PROGRESS to 0,
            ),
        )
        var progress = 0

        return withContext(Dispatchers.IO) {
            try {
                val connection = URL(imageUrl).openConnection()
                val contentLength = connection.contentLength
                var totalBytesCopied: Long = 0

                connection.getInputStream().use { input ->
                    FileOutputStream(outputFile).use { output ->
                        val buffer = ByteArray(8 * 1024)
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            totalBytesCopied += bytesRead
                            progress = (totalBytesCopied * 100 / contentLength).toInt()
                            setProgress(
                                workDataOf(
                                    KEY_INPUT_URL to imageUrl,
                                    KEY_OUTPUT_URI to outputFile.toURI().toString(),
                                    KEY_PROGRESS to progress,
                                ),
                            )
//                            updateNotification(fileName, "$progress%")
                        }
                    }
                }

                Result.success(
                    workDataOf(
                        KEY_INPUT_URL to imageUrl,
                        KEY_OUTPUT_URI to outputFile.toURI().toString(),
                        KEY_PROGRESS to PROGRESS_SUCCESSFUL,
                    ),
                )
            } catch (e: Exception) {
                Timber.e(e, "Download file $imageUrl failed")
                outputFile.delete()
                Result.failure(
                    workDataOf(
                        KEY_INPUT_URL to imageUrl,
                        KEY_OUTPUT_URI to outputFile.toURI().toString(),
                        KEY_PROGRESS to progress,
                    ),
                )
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = applicationContext.getString(R.string.download_channel)
            val descriptionText = applicationContext.getString(R.string.download_channel_desc)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel =
                NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(
        fileName: String,
        text: String,
    ) = NotificationCompat
        .Builder(applicationContext, CHANNEL_ID)
        .setContentTitle(applicationContext.getString(R.string.downloading, fileName))
        .setContentText(text)
        .setSmallIcon(android.R.drawable.stat_sys_download)
        .setOngoing(true)
        .build()

    private fun updateNotification(
        fileName: String,
        text: String,
    ) {
        val notification = createNotification(fileName, text)
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}

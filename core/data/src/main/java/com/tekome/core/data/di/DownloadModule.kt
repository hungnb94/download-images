package com.tekome.core.data.di

import com.tekome.core.data.downloader.ImageDownloadManager
import com.tekome.core.data.downloader.WorkManagerImageDownloadManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DownloadModule {
    @Binds
    internal abstract fun bindsImageDownloader(imageDownloader: WorkManagerImageDownloadManager): ImageDownloadManager
}

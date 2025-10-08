package com.tekome.core.data.di

import com.tekome.core.data.repository.ImageRepository
import com.tekome.core.data.repository.OfflineFirstImageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    internal abstract fun bindsTopicRepository(imagesRepository: OfflineFirstImageRepository): ImageRepository
}

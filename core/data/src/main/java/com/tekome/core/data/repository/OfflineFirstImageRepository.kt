package com.tekome.core.data.repository

import com.tekome.core.data.model.asEntity
import com.tekome.core.database.asExternalModel
import com.tekome.core.model.Image
import com.tekome.core.network.service.ImageApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class OfflineFirstImageRepository(
    private val apiService: ImageApiService,
) : ImageRepository {
    override suspend fun getImages(): Flow<List<Image>> =
        flow {
            val entities = apiService.getImages().map { it.asEntity() }
            emit(entities.map { it.asExternalModel() })
        }
}

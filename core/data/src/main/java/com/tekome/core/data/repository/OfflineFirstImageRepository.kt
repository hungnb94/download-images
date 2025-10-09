package com.tekome.core.data.repository

import com.tekome.core.data.model.asEntity
import com.tekome.core.database.asExternalModel
import com.tekome.core.model.Image
import com.tekome.core.network.ImageNetworkDatasource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class OfflineFirstImageRepository
    @Inject
    constructor(
        private val network: dagger.Lazy<ImageNetworkDatasource>,
    ) : ImageRepository {
        override fun getImages(): Flow<List<Image>> =
            flow {
                val response = network.get().getImages()
                val entities = response.map { it.asEntity() }
                emit(entities.map { it.asExternalModel() })
            }
    }

package com.tekome.core.data.repository

import com.tekome.core.data.model.asEntity
import com.tekome.core.database.asExternalModel
import com.tekome.core.model.Image
import com.tekome.core.network.ImageNetworkDatasource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

internal class OfflineFirstImageRepository
    @Inject
    constructor(
        private val network: ImageNetworkDatasource,
    ) : ImageRepository {
        override fun getImages(): Flow<List<Image>> =
            flow {
                Timber.d("getImages: start")
                val response = network.getImages()
                Timber.d("response: ${response.size} $response")
                val entities = response.map { it.asEntity() }
                Timber.d("entities: ${entities.size} $entities")
                emit(entities.map { it.asExternalModel() })
            }
    }

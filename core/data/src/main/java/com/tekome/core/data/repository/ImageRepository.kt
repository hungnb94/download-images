package com.tekome.core.data.repository

import com.tekome.core.model.Image
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    fun getImages(): Flow<List<Image>>
}

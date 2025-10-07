package com.tekome.core.network

import com.tekome.core.network.model.ImageResponse

interface ImageNetworkDatasource {
    suspend fun getImages(): List<ImageResponse>
}

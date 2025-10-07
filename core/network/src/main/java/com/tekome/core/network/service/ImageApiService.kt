package com.tekome.core.network.service

import com.tekome.core.network.model.ImageResponse
import retrofit2.http.GET

interface ImageApiService {
    @GET("files")
    suspend fun getImages(): List<ImageResponse>
}

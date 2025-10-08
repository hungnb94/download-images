package com.tekome.core.network.retrofit

import com.tekome.core.network.ImageNetworkDatasource
import com.tekome.core.network.model.ImageResponse
import com.tekome.core.network.service.ImageApiService
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

private const val BASE_URL = "https://68e223708943bf6bb3c5be40.mockapi.io"

@Singleton
class RetrofitImageNetwork
    @Inject
    constructor(
        networkJson: Json,
        okhttpCallFactory: dagger.Lazy<Call.Factory>,
    ) : ImageNetworkDatasource {

        private val networkApiService =
            Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .callFactory { okhttpCallFactory.get().newCall(it) }
                .addConverterFactory(networkJson.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(ImageApiService::class.java)

        override suspend fun getImages(): List<ImageResponse> = networkApiService.getImages()
    }

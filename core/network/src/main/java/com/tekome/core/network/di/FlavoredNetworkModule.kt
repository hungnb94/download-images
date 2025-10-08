package com.tekome.core.network.di

import com.tekome.core.network.ImageNetworkDatasource
import com.tekome.core.network.retrofit.RetrofitImageNetwork
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface FlavoredNetworkModule {
    @Binds
    fun binds(network: RetrofitImageNetwork): ImageNetworkDatasource
}

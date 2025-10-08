package com.tekome.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageResponse(
    @SerialName("id")
    val id: String,
    @SerialName("createAt")
    val createAt: String = "",
    @SerialName("url")
    val url: String = "",
    @SerialName("updateAt")
    val updateAt: String = "",
)

package com.tekome.core.data.model

import com.tekome.core.database.ImageEntity
import com.tekome.core.network.model.ImageResponse

fun ImageResponse.asEntity() =
    ImageEntity(
        id = id,
        createAt = createAt,
        url = url,
        updateAt = updateAt,
    )

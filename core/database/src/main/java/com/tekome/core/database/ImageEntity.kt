package com.tekome.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tekome.core.model.Image

@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey
    val id: String,
    val createAt: String,
    val url: String,
    val updateAt: String,
)

fun ImageEntity.asExternalModel() =
    Image(
        id = id,
        url = url,
    )

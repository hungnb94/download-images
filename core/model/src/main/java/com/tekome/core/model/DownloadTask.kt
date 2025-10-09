package com.tekome.core.model

import java.util.UUID

data class DownloadTask(
    val id: UUID,
    val inputUrl: String,
    val outputUri: String,
    val progress: Int = 0,
    val isFinished: Boolean = false,
)

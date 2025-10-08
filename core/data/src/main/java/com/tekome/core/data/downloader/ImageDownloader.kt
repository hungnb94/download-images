package com.tekome.core.data.downloader

import java.util.UUID

interface ImageDownloader {
    fun downloadImage(
        url: String,
        fileName: String,
    ): UUID
}

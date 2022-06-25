package com.burbon.photosync.data.requests

data class RequestSendImages(val phoneId: String, val photoList: List<ImageEncoding>)

data class ImageEncoding(
    val photoId: String,
    val photoEncoding: PhotoEncoding,
    val encodingTechnique: String
)

data class PhotoEncoding(val mine: String, val data: String)

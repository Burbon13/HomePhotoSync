package com.burbon.photosync.data.requests

data class RequestWhichImagesToSend(val phoneId: String, val photoIdList: List<String>)

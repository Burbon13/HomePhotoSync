package com.burbon.photosync.ui.operations

import android.util.Base64
import android.util.Base64OutputStream
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burbon.photosync.data.ImageEncoding
import com.burbon.photosync.data.LocalStorageSource
import com.burbon.photosync.data.PhotoDataSource
import com.burbon.photosync.data.PhotoEncoding
import com.burbon.photosync.data.RequestImages
import com.burbon.photosync.data.ResultImagesToSend
import com.burbon.photosync.data.ResultMessage
import com.burbon.photosync.data.Result
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class OperationsViewModel() : ViewModel() {

    private val _testMessage = MutableLiveData<String>()
    val testMessage = _testMessage as LiveData<String>

    private val phoneId = "testid" // MOCK, replace!
    private var cachedPhotoFiles: List<File>? = null

    init {
        viewModelScope.launch {
            val result = PhotoDataSource.getTest()
            if (result.succeeded) {
                val resultString = (result as Result.Success<ResultMessage>).data
                _testMessage.value = resultString.message
            } else {
                _testMessage.value = "Something went wrong :("
            }
        }
    }

    fun getLocalPhotos() {
        viewModelScope.launch {
            val photoResults = LocalStorageSource.getPhotoNames()
            cachedPhotoFiles = photoResults
            val photoIds = photoResults.map { photoFile -> photoFile.name }
            val result = PhotoDataSource.getImagesToSend(phoneId, photoIds)
            if (result.succeeded) {
                val resultImagesToSend = (result as Result.Success<ResultImagesToSend>).data
                _testMessage.value = "Images to send: " + resultImagesToSend.photoIdList.size
            } else {
                _testMessage.value = "Something went wrong :("
            }
        }
    }

    // Thanks to https://stackoverflow.com/questions/28758014/how-to-convert-a-file-to-base64
    private fun convertImageFileToBase64(imageFile: File): String {
        return ByteArrayOutputStream().use { outputStream ->
            Base64OutputStream(outputStream, Base64.DEFAULT).use { base64FilterStream ->
                imageFile.inputStream().use { inputStream ->
                    inputStream.copyTo(base64FilterStream)
                }
            }
            return@use outputStream.toString()
        }
    }

    fun sendLocalPhotos() {
        viewModelScope.launch {
            cachedPhotoFiles?.let {
                val base64Photos = it.map { photo ->
                    val base64Encoding = convertImageFileToBase64(photo)
                    val photoEncoding = PhotoEncoding("image/jpeg", base64Encoding)
                    ImageEncoding(photo.name, photoEncoding, "base64")
                }
                val requestImages = RequestImages(phoneId, base64Photos)
                val result = PhotoDataSource.putImages(requestImages)
            }
        }
    }
}

package com.burbon.photosync.ui.operations

import android.util.Base64
import android.util.Base64OutputStream
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burbon.photosync.data.requests.ImageEncoding
import com.burbon.photosync.data.LocalStorageSource
import com.burbon.photosync.data.PhotoDataSource
import com.burbon.photosync.data.requests.PhotoEncoding
import com.burbon.photosync.data.requests.RequestSendImages
import com.burbon.photosync.data.results.ResultWhichImagesToSend
import com.burbon.photosync.data.results.ResultTest
import com.burbon.photosync.data.results.Result
import com.burbon.photosync.data.results.ResultSendImages
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class OperationsViewModel() : ViewModel() {

    private val _testMessage = MutableLiveData<String>()
    val testMessage = _testMessage as LiveData<String>

    private val phoneId = "testid" // MOCK, replace!
    private var cachedPhotoFiles: List<File>? = null
    private var requestedImages: Set<String> = HashSet<String>()

    init {
        viewModelScope.launch {
            val result = PhotoDataSource.getTest()
            if (result.succeeded) {
                val resultString = (result as Result.Success<ResultTest>).data
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
                val resultImagesToSend = (result as Result.Success<ResultWhichImagesToSend>).data
                requestedImages = resultImagesToSend.photoIdList.toSet()
                _testMessage.value = "Images to send: " + resultImagesToSend.photoIdList.size
            } else {
                _testMessage.value = "Something went wrong :("
            }
        }
    }

    fun sendLocalPhotos() {
        viewModelScope.launch {
            cachedPhotoFiles?.let {
                val base64Photos = it
                    .filter { photo -> requestedImages.contains(photo.name) }
                    .map { photo ->
                        val base64Encoding = convertImageFileToBase64(photo)
                        val photoEncoding = PhotoEncoding("image/jpeg", base64Encoding)
                        ImageEncoding(photo.name, photoEncoding, "base64")
                    }
                val requestImages = RequestSendImages(phoneId, base64Photos)
                val result = PhotoDataSource.putImages(requestImages)
                if (result.succeeded) {
                    val resultImages = (result as Result.Success<ResultSendImages>).data
                    _testMessage.value = resultImages.message
                } else {
                    _testMessage.value = "Something went wrong :("
                }
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
}

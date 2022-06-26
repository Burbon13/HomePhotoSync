package com.burbon.photosync.ui.operations

import android.content.SharedPreferences
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Log
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
import com.burbon.photosync.utils.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File

class OperationsViewModel(sharedPreferences: SharedPreferences) : ViewModel() {

    // TODO: Should not post the text directly, that is the responsability of the view.
    // TODO: Replace with some IDs
    private val _testMessage = MutableLiveData<String>()
    val testMessage = _testMessage as LiveData<String>

    private var cachedPhotoFiles: List<File>? = null
    private var requestedImages: Set<String> = HashSet()

    private var phoneId: String = sharedPreferences.getString("user_id", "") ?: "DEFAULT"

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val result = PhotoDataSource.getTest()
            if (result.succeeded) {
                val resultString = (result as Result.Success<ResultTest>).data
                _testMessage.postValue(resultString.message)
            } else {
                _testMessage.postValue("Something went wrong :(")
            }
        }
    }

    private val somethingWentWrongMessage = "Something went wrong :(" // Replace with R string or id

    fun getLocalPhotos() {
        viewModelScope.launch(Dispatchers.IO) {
            val photoResults = LocalStorageSource.getPhotoNames()
            cachedPhotoFiles = photoResults
            val photoIds = photoResults.map { photoFile -> photoFile.name }
            val result = PhotoDataSource.getImagesToSend(phoneId, photoIds)
            if (result.succeeded) {
                val resultImagesToSend = (result as Result.Success<ResultWhichImagesToSend>).data
                requestedImages = resultImagesToSend.photoIdList.toSet()
                _testMessage.postValue("Images to send: " + resultImagesToSend.photoIdList.size)
            } else {
                _testMessage.postValue(somethingWentWrongMessage)
            }
        }
    }

    fun sendLocalPhotos() {
        Log.i(TAG, "Sending Local Photos")
        viewModelScope.launch(Dispatchers.IO) {
            cachedPhotoFiles?.let {
                Log.d(TAG, "Converting photos to base64 encoding")
                val base64Photos = it
                    .filter { photo -> requestedImages.contains(photo.name) }
                    .map { photo ->
                        val base64Encoding = convertImageFileToBase64(photo)
                        val photoEncoding = PhotoEncoding("image/jpeg", base64Encoding)
                        ImageEncoding(photo.name, photoEncoding, "base64")
                    }
                val requestImages = RequestSendImages(phoneId, base64Photos)
                Log.d(TAG, "Sending local images to PhotoDataSource")
                val result = PhotoDataSource.putImages(requestImages)
                if (result.succeeded) {
                    Log.i(TAG, "Images sent successfully")
                    val resultImages = (result as Result.Success<ResultSendImages>).data
                    _testMessage.postValue(resultImages.message)
                } else {
                    Log.e(TAG, "Sending local images failed: $result")
                    _testMessage.postValue(somethingWentWrongMessage)
                }
            }
        }
    }

    // TODO: Maybe extract to some utils file? Along with the mapping done above.
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

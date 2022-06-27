package com.burbon.photosync.data

import android.util.Base64
import android.util.Base64OutputStream
import android.util.Log
import com.burbon.photosync.data.requests.ImageEncoding
import com.burbon.photosync.data.requests.PhotoEncoding
import com.burbon.photosync.data.requests.RequestSendImages
import com.burbon.photosync.utils.TAG
import java.io.ByteArrayOutputStream
import java.io.File

object PhotoServices {

    private val photosPath = "/sdcard/DCIM/CameraTest" // Use String from preferences

    enum class BackendConnection {
        WORKING,
        NOT_WORKING
    }

    suspend fun testBackendConnection(): BackendConnection {
        Log.d(TAG, "Testing backend connection")
        val result = PhotoDataSource.getTest()
        return if (result.isSuccess) {
            Log.i(TAG, "Backend connection working: $result")
            BackendConnection.WORKING
        } else {
            Log.e(TAG, "Backend connection error: $result")
            BackendConnection.NOT_WORKING
        }
    }

    suspend fun getLocalFilesNotSynced(phoneId: String): Result<Set<File>> {
        val localPhotosResult = LocalStorageSource.getPhotoFiles(photosPath)
        if (localPhotosResult.isFailure) {
            Log.e(TAG, "Could not get local photos.")
            return Result.failure(PhotoServicesException("Could not get local photos"))
        }

        val localPhotos = localPhotosResult.getOrThrow()
        Log.d(TAG, "Found ${localPhotos.size} local photos")
        val photosId = localPhotos.map { photoFile -> photoFile.name }
        val result = PhotoDataSource.getImagesToSend(phoneId, photosId)

        if (result.isFailure) {
            Log.e(TAG, "Could not retrieve images to send: $result")
            return Result.failure(PhotoServicesException("Could not communicate with the backend"))
        }

        val resultImagesToSend = result.getOrThrow()
        val setOfNames = resultImagesToSend.photoIdList.toSet()
        val notSyncedPhotos = localPhotos.filter { photo ->
            setOfNames.contains(photo.name)
        }.toSet()
        Log.d(TAG, "Found ${notSyncedPhotos.size} photos that are not synced")

        return Result.success(notSyncedPhotos)
    }

    suspend fun sendPhotosToSync(phoneId: String, photos: Set<File>): Result<Unit> {
        Log.d(TAG, "Converting photos to base64 encoding")
        val base64Photos = photos
            .map { photo ->
                val base64Encoding = convertImageFileToBase64(photo)
                val photoEncoding = PhotoEncoding("image/jpeg", base64Encoding)
                ImageEncoding(photo.name, photoEncoding, "base64")
            }
        val requestImages = RequestSendImages(phoneId, base64Photos)
        Log.d(TAG, "Sending local images to PhotoDataSource")
        val result = PhotoDataSource.putImages(requestImages)
        if (result.isSuccess) {
            return Result.success(Unit)
        }

        Log.e(TAG, "Could not synchronize photos: $result")
        return Result.failure(PhotoServicesException("Could not synchronize photos"))
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

class PhotoServicesException(message: String) : Throwable(message)
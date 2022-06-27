package com.burbon.photosync.data

import android.util.Log
import com.burbon.photosync.utils.TAG
import java.io.File
import java.lang.Exception

object LocalStorageSource {

    fun getPhotoFiles(photosPath: String): Result<Set<File>> {
        Log.i(TAG, "Retrieve photo names")
        return try {
            val photoFiles = File(photosPath).listFiles()?.asList()
            if (photoFiles != null) {
                return Result.success(photoFiles.toSet())
            }
            return Result.failure(
                LocalStorageSourceException("Could not retrieve files at path \"$photosPath\"")
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception on retrieving photo files: $e")
            Result.failure(e)
        }
    }
}

class LocalStorageSourceException(message: String) : Throwable(message)

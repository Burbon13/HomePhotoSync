package com.burbon.photosync.data

import android.util.Log
import com.burbon.photosync.utils.TAG
import java.io.File
import java.lang.Exception

object LocalStorageSource {

    // The list probably is incomplete
    private val imageExtensions = listOf("jpg", "png", "gif", "jpeg")

    fun getFiles(photosPath: String, onlyImages: Boolean = true): Result<Set<File>> {
        Log.i(TAG, "Retrieve photo names")
        return try {
            val photoFiles = File(photosPath).listFiles()?.asList()?.filter { file ->
                file.isFile  // Filter out folders, does not recursively search for photos!
            }?.filter { file ->
                // This is a primitive way of checking if the file is an image, the extension
                // does not guarantee that the file is actually a picture.
                !onlyImages || imageExtensions.any { ext ->
                    file.extension.lowercase() == ext
                }
            }
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

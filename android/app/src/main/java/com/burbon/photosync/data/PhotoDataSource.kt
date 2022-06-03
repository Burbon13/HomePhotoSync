package com.burbon.photosync.data

import android.util.Log
import com.burbon.photosync.utils.TAG
import retrofit2.http.GET
import java.lang.Exception


object PhotoDataSource {
    interface PhotoService {
        @GET("/photo-sync/api/test")
        suspend fun getTest(): ResultMessage
    }

    private val photoService: PhotoService = Api.retrofit.create(
        PhotoService::class.java
    )

    suspend fun getTest(): Result<ResultMessage> {
        return try {
            val result = photoService.getTest()
            Result.Success(result)
        } catch (e: Exception) {
            Log.w(TAG, e.toString())
            Result.Error("Something went wrong :(")
        }
    }
}

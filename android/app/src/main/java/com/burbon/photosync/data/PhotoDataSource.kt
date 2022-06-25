package com.burbon.photosync.data

import android.util.Log
import com.burbon.photosync.utils.TAG
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import java.lang.Exception


object PhotoDataSource {
    interface PhotoService {
        @GET("/photo-sync/api/test")
        suspend fun getTest(): ResultMessage

        @POST("/photo-sync/api/photos")
        suspend fun getImagesToSend(@Body request: RequestImagesToSend): ResultImagesToSend

        @PUT("/photo-sync/api/photos")
        suspend fun sendImages(@Body request: RequestImages): ResultImages
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

    suspend fun getImagesToSend(
        phoneId: String,
        photoIdList: List<String>
    ): Result<ResultImagesToSend> {
        return try {
            Log.d(TAG, "Try get images to send")
            val result = photoService.getImagesToSend(RequestImagesToSend(phoneId, photoIdList))
            Result.Success(result)
        } catch (e: Exception) {
            Log.w(TAG, e.toString())
            Result.Error("Something went wrong :(")
        }
    }

    suspend fun putImages(request: RequestImages): Result<ResultImages> {
        return try {
            Log.d(TAG, "Try send images " + request.photoList.size + " images")
            val result = photoService.sendImages(request)
            Result.Success(result)
        } catch (e: Exception) {
            Log.w(TAG, e.toString())
            Result.Error("Something went wrong :(")
        }
    }
}

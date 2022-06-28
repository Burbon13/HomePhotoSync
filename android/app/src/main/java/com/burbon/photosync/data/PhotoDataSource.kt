package com.burbon.photosync.data

import android.util.Log
import com.burbon.photosync.data.requests.RequestSendImages
import com.burbon.photosync.data.requests.RequestWhichImagesToSend
import com.burbon.photosync.data.results.ResultSendImages
import com.burbon.photosync.data.results.ResultWhichImagesToSend
import com.burbon.photosync.data.results.ResultTest
import com.burbon.photosync.utils.TAG
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.lang.Exception

object PhotoDataSource {
    interface PhotoService {
        @GET("/photo-sync/api/test")
        suspend fun getTest(): ResultTest

        @POST("{fullUrl}/photo-sync/api/photos")
        suspend fun getImagesToSend(
            @Path(value = "fullUrl", encoded = true) fullUrl: String,
            @Body request: RequestWhichImagesToSend
        ): ResultWhichImagesToSend

        @PUT("{fullUrl}/photo-sync/api/photos")
        suspend fun sendImages(
            @Path(value = "fullUrl", encoded = true) fullUrl: String,
            @Body request: RequestSendImages
        ): ResultSendImages
    }

    private val photoService: PhotoService = Api.retrofit.create(
        PhotoService::class.java
    )

    suspend fun getTest(): Result<ResultTest> {
        return try {
            val result = photoService.getTest()
            Result.success(result)
        } catch (e: Exception) {
            Log.w(TAG, e.toString())
            Result.failure(e)
        }
    }

    suspend fun getImagesToSend(
        url: String,
        phoneId: String,
        photoIdList: List<String>
    ): Result<ResultWhichImagesToSend> {
        return try {
            Log.d(TAG, "Try get images to send from $url")
            val result =
                photoService.getImagesToSend(url, RequestWhichImagesToSend(phoneId, photoIdList))
            Log.d(TAG, "Get images to send success: $result")
            Result.success(result)
        } catch (e: Exception) {
            Log.w(TAG, e.toString())
            Result.failure(e)
        }
    }

    suspend fun putImages(url: String, request: RequestSendImages): Result<ResultSendImages> {
        return try {
            Log.d(TAG, "Try send images " + request.photoList.size + " images to $url")
            val result = photoService.sendImages(url, request)
            Log.d(TAG, "Images sent request success: $result")
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            Result.failure(e)
        }
    }
}

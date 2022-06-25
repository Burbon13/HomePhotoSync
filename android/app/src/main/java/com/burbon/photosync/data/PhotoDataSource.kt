package com.burbon.photosync.data

import android.util.Log
import com.burbon.photosync.data.requests.RequestSendImages
import com.burbon.photosync.data.requests.RequestWhichImagesToSend
import com.burbon.photosync.data.results.Result
import com.burbon.photosync.data.results.ResultSendImages
import com.burbon.photosync.data.results.ResultWhichImagesToSend
import com.burbon.photosync.data.results.ResultTest
import com.burbon.photosync.utils.TAG
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import java.lang.Exception

object PhotoDataSource {
    interface PhotoService {
        @GET("/photo-sync/api/test")
        suspend fun getTest(): ResultTest

        @POST("/photo-sync/api/photos")
        suspend fun getImagesToSend(@Body request: RequestWhichImagesToSend): ResultWhichImagesToSend

        @PUT("/photo-sync/api/photos")
        suspend fun sendImages(@Body request: RequestSendImages): ResultSendImages
    }

    private val photoService: PhotoService = Api.retrofit.create(
        PhotoService::class.java
    )

    suspend fun getTest(): Result<ResultTest> {
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
    ): Result<ResultWhichImagesToSend> {
        return try {
            Log.d(TAG, "Try get images to send")
            val result = photoService.getImagesToSend(RequestWhichImagesToSend(phoneId, photoIdList))
            Log.d(TAG, "Get images to send success")
            Result.Success(result)
        } catch (e: Exception) {
            Log.w(TAG, e.toString())
            Result.Error("Something went wrong :(")
        }
    }

    suspend fun putImages(request: RequestSendImages): Result<ResultSendImages> {
        return try {
            Log.d(TAG, "Try send images " + request.photoList.size + " images")
            val result = photoService.sendImages(request)
            Log.d(TAG, "Images sent request success")
            Result.Success(result)
        } catch (e: Exception) {
            Log.w(TAG, e.toString())
            Result.Error("Something went wrong :(")
        }
    }
}

package com.burbon.photosync.data

import android.util.Log
import com.burbon.photosync.utils.TAG
import java.io.File

object LocalStorageSource {

    private val photosPath = "/sdcard/DCIM/CameraTest" // TODO: Make it modifiable

    fun getPhotoNames(): List<File> {
        Log.i(TAG, "Retrieve photo names")
        val photoList = File(photosPath).listFiles().asList()
        return photoList
    }
}

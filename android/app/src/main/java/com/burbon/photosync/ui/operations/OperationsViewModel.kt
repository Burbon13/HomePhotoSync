package com.burbon.photosync.ui.operations

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burbon.photosync.data.PhotoServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class OperationsViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {

    private val _operationsStatus = MutableLiveData(OperationsStatus.IDLE)
    val operationsStatus = _operationsStatus as LiveData<OperationsStatus>

    private val _currentIndexOfSync = MutableLiveData<Int>()
    val currentIndexOfSync = _currentIndexOfSync as LiveData<Int>

    private var cachedPhotos: Set<File>? = null

    enum class OperationsStatus {
        IDLE,
        TESTING_CONNECTION,
        TESTING_CONNECTION_SUCCESS,
        TESTING_CONNECTION_FAILURE,
        RETRIEVE_NOT_SYNCED_PHOTOS,
        RETRIEVE_NOT_SYNCED_PHOTOS_SUCCESS,
        RETRIEVE_NOT_SYNCED_PHOTOS_FAILURE,
        SYNCING_PHOTOS,
        SYNCING_PHOTOS_SUCCESS,
        SYNCING_PHOTOS_FAILURE
    }

    private val executingStatuses = setOf(
        OperationsStatus.TESTING_CONNECTION,
        OperationsStatus.RETRIEVE_NOT_SYNCED_PHOTOS,
        OperationsStatus.SYNCING_PHOTOS
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _operationsStatus.postValue(OperationsStatus.TESTING_CONNECTION)
            val backendWorking = PhotoServices.testBackendConnection()
            if (backendWorking == PhotoServices.BackendConnection.WORKING) {
                _operationsStatus.postValue(OperationsStatus.TESTING_CONNECTION_SUCCESS)
            } else {
                _operationsStatus.postValue(OperationsStatus.TESTING_CONNECTION_FAILURE)
            }
        }
    }

    fun getLocalPhotos() {
        viewModelScope.launch(Dispatchers.IO) {
            _operationsStatus.postValue(OperationsStatus.RETRIEVE_NOT_SYNCED_PHOTOS)
            val localFilesNotSyncedResult =
                PhotoServices.getLocalFilesNotSynced(
                    userId(),
                    ipAddress(),
                    folderPath(),
                    syncOnlyPhotos()
                )
            localFilesNotSyncedResult.onSuccess {
                cachedPhotos = it
                _operationsStatus.postValue(OperationsStatus.RETRIEVE_NOT_SYNCED_PHOTOS_SUCCESS)
            }.onFailure {
                _operationsStatus.postValue(OperationsStatus.RETRIEVE_NOT_SYNCED_PHOTOS_FAILURE)
            }
        }
    }

    @Deprecated("Will cause out of memory exceptions")
    fun sendLocalPhotos() {
        viewModelScope.launch(Dispatchers.IO) {
            cachedPhotos?.let {
                _operationsStatus.postValue(OperationsStatus.SYNCING_PHOTOS)
                val syncPhotosResult = PhotoServices.sendPhotosToSync(userId(), ipAddress(), it)
                syncPhotosResult.onSuccess {
                    cachedPhotos = emptySet()
                    _operationsStatus.postValue(OperationsStatus.SYNCING_PHOTOS_SUCCESS)
                }.onFailure {
                    _operationsStatus.postValue(OperationsStatus.SYNCING_PHOTOS_FAILURE)
                }
            }
        }
    }

    fun sendLocalPhotosOneByOne() {
        viewModelScope.launch(Dispatchers.IO) {
            cachedPhotos?.let {
                _operationsStatus.postValue(OperationsStatus.SYNCING_PHOTOS)
                val syncPhotosResult =
                    PhotoServices.sendPhotosToSyncOneByOne(userId(), ipAddress(), it) { index ->
                        _currentIndexOfSync.postValue(index)
                    }
                syncPhotosResult.onSuccess {
                    cachedPhotos = emptySet()
                    _operationsStatus.postValue(OperationsStatus.SYNCING_PHOTOS_SUCCESS)
                }.onFailure {
                    _operationsStatus.postValue(OperationsStatus.SYNCING_PHOTOS_FAILURE)
                }
            }
        }
    }

    fun operationsStatusExecuting(status: OperationsStatus): Boolean {
        return executingStatuses.contains(status)
    }

    fun getNumberOfPhotosToSync(): Int {
        return cachedPhotos?.size ?: 0
    }

    // Maybe not the best approach for using up-to-date preferences.
    // Todo: maybe look on how to inject the preferences or define some listeners for changes?

    private fun userId(): String {
        return sharedPreferences.getString("user_id", "Alice") ?: "Alice"
    }

    private fun ipAddress(): String {
        return sharedPreferences.getString("server_ip", "192.168.1.2") ?: "192.168.1.2"
    }

    private fun folderPath(): String {
        // Todo: check Environment.getExternalStorageDirectory()
        return sharedPreferences.getString("folder_path", "/sdcard/DCIM/CameraTest")
            ?: "/sdcard/DCIM/CameraTest"
    }

    private fun syncOnlyPhotos(): Boolean {
        return sharedPreferences.getBoolean("sync_all_files", true).not()
    }
}

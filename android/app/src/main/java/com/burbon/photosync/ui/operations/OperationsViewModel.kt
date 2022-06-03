package com.burbon.photosync.ui.operations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burbon.photosync.data.PhotoDataSource
import com.burbon.photosync.data.Result
import com.burbon.photosync.data.ResultMessage
import kotlinx.coroutines.launch


class OperationsViewModel() : ViewModel() {

    private val _testMessage = MutableLiveData<String>()
    val testMessage = _testMessage as LiveData<String>

    init {
        viewModelScope.launch {
            val result = PhotoDataSource.getTest()
            if (result.succeeded) {
                val resultString = (result as Result.Success<ResultMessage>).data
                _testMessage.value = resultString.message
            } else {
                _testMessage.value = "Something went wrong :("
            }
        }
    }
}

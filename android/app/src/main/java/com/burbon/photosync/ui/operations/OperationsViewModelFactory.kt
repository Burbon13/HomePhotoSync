package com.burbon.photosync.ui.operations

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class OperationsViewModelFactory(private val sharedPreferences: SharedPreferences) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OperationsViewModel::class.java)) {
            return OperationsViewModel(sharedPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class. Expected OperationsViewModel.")
    }
}

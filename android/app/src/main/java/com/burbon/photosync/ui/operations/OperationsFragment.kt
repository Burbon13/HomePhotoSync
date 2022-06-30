package com.burbon.photosync.ui.operations

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.burbon.photosync.R
import com.burbon.photosync.databinding.FragmentMainBinding
import com.burbon.photosync.utils.TAG

class OperationsFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private lateinit var _operationsViewModel: OperationsViewModel

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory =
            OperationsViewModelFactory(PreferenceManager.getDefaultSharedPreferences(requireContext()))
        _operationsViewModel =
            ViewModelProvider(this, viewModelFactory).get(OperationsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _operationsViewModel.operationsStatus.observe(viewLifecycleOwner, { status ->
            adjustUiActivation(status)
            updateUiText(status)
            updateProgressBar(status)
        })

        _operationsViewModel.currentIndexOfSync.observe(viewLifecycleOwner, { index ->
            binding.opsExtraInfoTextView.text = getString(
                R.string.synced_nr_photos,
                index,
                _operationsViewModel.getNumberOfPhotosToSync()
            )
            binding.progressBarSendPhotos.max = _operationsViewModel.getNumberOfPhotosToSync()
            binding.progressBarSendPhotos.setProgress(index, true)
        })

        binding.buttonGetPhotos.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                Log.i(TAG, "Requesting permissions")
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    123  // TODO: Refactor
                )
            } else {
                Log.i(TAG, "Permissions already accepted")
                _operationsViewModel.getLocalPhotos()
            }
        }

        binding.buttonSendPhotosOneByOne.setOnClickListener {
            _operationsViewModel.sendLocalPhotosOneByOne()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun adjustUiActivation(status: OperationsViewModel.OperationsStatus) {
        // Block buttons if executing operation
        if (_operationsViewModel.operationsStatusExecuting(status)) {
            binding.buttonGetPhotos.isClickable = false
            binding.buttonSendPhotosOneByOne.isClickable = false
        } else {
            binding.buttonGetPhotos.isClickable = true
            binding.buttonSendPhotosOneByOne.isClickable = true
        }

        binding.buttonSendPhotosOneByOne.isEnabled =
            _operationsViewModel.getNumberOfPhotosToSync() != 0
    }

    private fun updateUiText(status: OperationsViewModel.OperationsStatus) {
        if (status == OperationsViewModel.OperationsStatus.TESTING_CONNECTION_SUCCESS) {
            binding.opsStatusTextView.text = getString(R.string.ops_status_connection_success)
        } else if (status == OperationsViewModel.OperationsStatus.TESTING_CONNECTION_FAILURE) {
            binding.opsStatusTextView.text = getString(R.string.ops_status_connection_failure)
        }

        if (status == OperationsViewModel.OperationsStatus.RETRIEVE_NOT_SYNCED_PHOTOS_SUCCESS) {
            binding.opsStatusTextView.text =
                getString(R.string.ops_status_retrieve_not_sync_photos_success)
            binding.opsExtraInfoTextView.text =
                getString(
                    R.string.ops_status_retrieve_not_sync_photos_info_number_photos,
                    _operationsViewModel.getNumberOfPhotosToSync()
                )
        } else if (status == OperationsViewModel.OperationsStatus.RETRIEVE_NOT_SYNCED_PHOTOS_FAILURE) {
            binding.opsStatusTextView.text =
                getString(R.string.ops_status_retrieve_not_sync_photos_failure)
        }

        if (status == OperationsViewModel.OperationsStatus.SYNCING_PHOTOS_SUCCESS) {
            binding.opsStatusTextView.text = getString(R.string.ops_status_sync_photos_success)
            binding.opsExtraInfoTextView.text = ""
        } else if (status == OperationsViewModel.OperationsStatus.SYNCING_PHOTOS_FAILURE) {
            binding.opsStatusTextView.text = getString(R.string.ops_status_sync_photos_failure)
            binding.opsExtraInfoTextView.text = ""
        } else if (status == OperationsViewModel.OperationsStatus.SYNCING_PHOTOS) {
            binding.opsStatusTextView.text = getString(R.string.ops_status_sync_photos_in_progress)
            binding.opsExtraInfoTextView.text = ""
        }
    }

    private fun updateProgressBar(status: OperationsViewModel.OperationsStatus) {
        if (status == OperationsViewModel.OperationsStatus.SYNCING_PHOTOS) {
            binding.progressBarSendPhotos.visibility = View.VISIBLE
        } else {
            binding.progressBarSendPhotos.visibility = View.GONE
        }
    }
}

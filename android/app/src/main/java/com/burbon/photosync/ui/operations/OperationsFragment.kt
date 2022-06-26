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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
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

        _operationsViewModel.testMessage.observe(viewLifecycleOwner, Observer { testResult ->
            binding.testTextView.text = testResult
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
                    123
                )
            } else {
                Log.i(TAG, "Permissions already accepted")
                _operationsViewModel.getLocalPhotos()
            }
        }

        binding.buttonSendPhotos.setOnClickListener {
            _operationsViewModel.sendLocalPhotos()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.burbon.photosync.ui.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.burbon.photosync.R
import java.util.regex.Pattern

class SettingsFragment : PreferenceFragmentCompat() {

    private val userIdPattern = Pattern.compile("[a-z][a-z][a-z][a-z][a-z]*")
    private val folderPathPattern = Pattern.compile("(/[a-zA-Z0-9]+)+")

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onStart() {
        super.onStart()
        findPreference<EditTextPreference>("server_ip")?.setOnBindEditTextListener { editText ->
            editText.isSingleLine = true
            editText.setSelection(editText.length())
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    s?.let {
                        if (!Patterns.IP_ADDRESS.matcher(it).matches()) {
                            editText.error = getString(R.string.invalid_ipv4_error_msg)
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
        }
        findPreference<EditTextPreference>("user_id")?.setOnBindEditTextListener { editText ->
            editText.isSingleLine = true
            editText.setSelection(editText.length())
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    s?.let {
                        if (!userIdPattern.matcher(it).matches()) {
                            editText.error = getString(R.string.invalid_userid_error_msg)
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
        }
        findPreference<EditTextPreference>("folder_path")?.setOnBindEditTextListener { editText ->
            editText.isSingleLine = true
            editText.setSelection(editText.length())
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    s?.let {
                        if (!folderPathPattern.matcher(it).matches()
                        ) {
                            editText.error = getString(R.string.invalid_path_error_msg)
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
        }
    }
}

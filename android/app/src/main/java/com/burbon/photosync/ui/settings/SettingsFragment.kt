package com.burbon.photosync.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.burbon.photosync.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}

package com.galacticai.networkpulse.ui.main.old.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.galacticai.networkpulse.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}
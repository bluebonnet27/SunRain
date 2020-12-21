package com.ti.sunrain.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.google.android.material.snackbar.Snackbar
import com.ti.sunrain.R
import com.ti.sunrain.SunRainApplication
import com.ti.sunrain.logic.ActivitySet.restartAllActivities

/**
 * @author: tihon
 * @date: 2020/12/21
 * @description:
 */
class SettingsFragment : PreferenceFragmentCompat(){

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = "settings"
        addPreferencesFromResource(R.xml.pref_settings)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        val covidPreference = findPreference<SwitchPreference>("covid19_switch")
        when(preference){
            covidPreference -> {
                Snackbar.make(requireActivity().findViewById(R.id.settingsLayout),"需要重启APP",Snackbar.LENGTH_SHORT)
                    .setAction("重启") {
                        restartAllActivities()
                    }
                    .show()
            }
        }

        return true
    }
}
package com.ti.sunrain.ui.settings

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.google.android.material.snackbar.Snackbar
import com.ti.sunrain.R
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

        val dateFormatPreference : ListPreference? = findPreference("forecastDateFormat_list")
        val notificationSwitchPreference : SwitchPreference? =findPreference("notification_switch")

        dateFormatPreference?.setOnPreferenceChangeListener { _, _ ->
            Snackbar.make(requireActivity().findViewById(R.id.settingsLayout),"下次刷新天气生效",Snackbar.LENGTH_SHORT)
                .show()
            true
        }

        notificationSwitchPreference?.setOnPreferenceChangeListener { _,_ ->
            Snackbar.make(requireActivity().findViewById(R.id.settingsLayout),"下次刷新天气生效",Snackbar.LENGTH_SHORT)
                .show()
            true
        }
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



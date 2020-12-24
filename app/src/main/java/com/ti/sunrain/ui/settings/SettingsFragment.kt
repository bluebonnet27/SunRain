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

        val covidPreference : SwitchPreference? = findPreference("covid19_switch")

        val dateFormatPreference : ListPreference? = findPreference("forecastDateFormat_list")
        val festivalBgPreference : SwitchPreference? = findPreference("festival_bg_switch")

        val notificationSwitchPreference : SwitchPreference? = findPreference("notification_switch")
        val notificationMoreInfoPreference : SwitchPreference? = findPreference("notification_moreinfo_switch")
        val notificationCanCancelPreference : SwitchPreference? = findPreference("notification_cancancel_switch")

        covidPreference?.setOnPreferenceChangeListener { _, _ ->
            Snackbar.make(requireActivity().findViewById(R.id.settingsLayout),"需要重启APP",Snackbar.LENGTH_SHORT)
                .setAction("重启") {
                    restartAllActivities()
                }
                .show()
            true
        }

        dateFormatPreference?.setOnPreferenceChangeListener { _, _ ->
            Snackbar.make(requireActivity().findViewById(R.id.settingsLayout),"下次刷新天气生效",Snackbar.LENGTH_SHORT)
                .show()
            true
        }

        festivalBgPreference?.setOnPreferenceChangeListener { _, _ ->
            Snackbar.make(requireActivity().findViewById(R.id.settingsLayout),"下次刷新天气生效",Snackbar.LENGTH_SHORT)
                .show()
            true
        }

        notificationSwitchPreference?.setOnPreferenceChangeListener { _, newValue ->
            Snackbar.make(requireActivity().findViewById(R.id.settingsLayout),"下次刷新天气生效",Snackbar.LENGTH_SHORT)
                .show()
            if(newValue == true){
                notificationMoreInfoPreference?.isEnabled = true
                notificationCanCancelPreference?.isEnabled = true
            }else{
                notificationMoreInfoPreference?.isEnabled = false
                notificationCanCancelPreference?.isEnabled = false
            }
            true
        }

        val preferences = SunRainApplication.context.getSharedPreferences("settings",0)
        val isNotificationSwitchPreferenceTrue = preferences.getBoolean("notification_switch",false)
        if(isNotificationSwitchPreferenceTrue){
            notificationMoreInfoPreference?.isEnabled = true
            notificationCanCancelPreference?.isEnabled = true
        }else{
            notificationMoreInfoPreference?.isEnabled = false
            notificationCanCancelPreference?.isEnabled = false
        }
    }
}



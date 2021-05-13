package com.ti.sunrain.ui.settings

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
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
        val forecastChartPreference : SwitchPreference? = findPreference("forecast_chart_switch")
        val titleRefreshPreference : SwitchPreference? = findPreference("title_refresh_switch")

        val notificationSwitchPreference : SwitchPreference? = findPreference("notification_switch")
        val notificationMoreInfoPreference : SwitchPreference? = findPreference("notification_moreinfo_switch")
        val notificationCanCancelPreference : SwitchPreference? = findPreference("notification_cancancel_switch")

        val serverAddressPreference : EditTextPreference? = findPreference("webdav_server_address_edit")
        val accountPreference :EditTextPreference? = findPreference("webdav_account_edit")
        val passwordPreference:EditTextPreference? = findPreference("webdav_password_edit")

        val darkmodePreference : ListPreference? = findPreference("others_darkmode_list")
        val languagePreference : ListPreference? = findPreference("others_language_list")

        if(isDarktheme(SunRainApplication.context)){
            val context = SunRainApplication.context

            covidPreference?.icon = getDrawable(context,R.drawable.baseline_warning_white_24dp)

            dateFormatPreference?.icon = getDrawable(context,R.drawable.baseline_date_range_white_24dp)
            festivalBgPreference?.icon = getDrawable(context,R.drawable.baseline_card_giftcard_white_24dp)
            forecastChartPreference?.icon = getDrawable(context,R.drawable.baseline_show_chart_white_24dp)
            titleRefreshPreference?.icon = getDrawable(context,R.drawable.baseline_title_white_24dp)

            notificationSwitchPreference?.icon = getDrawable(context,R.drawable.baseline_notifications_white_24dp)

            serverAddressPreference?.icon = getDrawable(context,R.drawable.baseline_computer_white_24dp)
            accountPreference?.icon = getDrawable(context,R.drawable.baseline_account_circle_white_24dp)
            passwordPreference?.icon = getDrawable(context, R.drawable.baseline_password_white_24dp)

            darkmodePreference?.icon = getDrawable(context,R.drawable.baseline_brightness_2_white_24dp)
            languagePreference?.icon = getDrawable(context,R.drawable.baseline_language_white_24dp)
        }

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

        forecastChartPreference?.setOnPreferenceChangeListener { _, _ ->
            Snackbar.make(requireActivity().findViewById(R.id.settingsLayout),"下次刷新天气生效",Snackbar.LENGTH_SHORT)
                .show()
            true
        }

        titleRefreshPreference?.setOnPreferenceChangeListener { _, _ ->
            Snackbar.make(requireActivity().findViewById(R.id.settingsLayout),"需要重启APP",Snackbar.LENGTH_SHORT)
                .setAction("重启") {
                    restartAllActivities()
                }
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

        //!!!
        val isNotificationSwitchPreferenceTrue = SunRainApplication.settingsPreference.getBoolean("notification_switch",false)
        if(isNotificationSwitchPreferenceTrue){
            notificationMoreInfoPreference?.isEnabled = true
            notificationCanCancelPreference?.isEnabled = true
        }else{
            notificationMoreInfoPreference?.isEnabled = false
            notificationCanCancelPreference?.isEnabled = false
        }

        notificationMoreInfoPreference?.setOnPreferenceChangeListener { _, _ ->
            Snackbar.make(requireActivity().findViewById(R.id.settingsLayout),"下次刷新天气生效",Snackbar.LENGTH_SHORT)
                .show()
            true
        }

        notificationCanCancelPreference?.setOnPreferenceChangeListener { _, _ ->
            Snackbar.make(requireActivity().findViewById(R.id.settingsLayout),"下次刷新天气生效",Snackbar.LENGTH_SHORT)
                .show()
            true
        }

        darkmodePreference?.setOnPreferenceChangeListener { _, _ ->
            AlertDialog.Builder(requireActivity())
                .setTitle("警告")
                .setMessage("与系统显示模式相反的显示模式会导致界面之间的切换出现闪屏" +
                        "现象，若您对闪烁现象感觉不适，建议保持显示模式跟随系统\n"+
                        "\n"+
                        "轻触“重启”以应用更改")
                .setPositiveButton("重启") { _, _ ->
                    restartAllActivities()
                }
                .setCancelable(false)
                .show()

            true
        }
    }

    fun isDarktheme(context: Context):Boolean{
        val flag = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return flag == Configuration.UI_MODE_NIGHT_YES
    }
}



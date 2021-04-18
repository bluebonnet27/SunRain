package com.ti.sunrain.ui.minutely

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import com.google.gson.Gson
import com.ti.sunrain.R
import com.ti.sunrain.SunRainApplication
import com.ti.sunrain.logic.ActivitySet
import com.ti.sunrain.logic.model.Weather
import kotlinx.android.synthetic.main.activity_minutely.*

class MinutelyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minutely)

        ActivitySet.addActivity(this)

        //darkmode
        when(SunRainApplication.settingsPreference.getString("others_darkmode_list","0")){
            "0" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            "1" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            "2" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        }

        setSupportActionBar(minutelyActivityToolBar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24dp)
        }

        val weatherJson = intent.getStringExtra("weather")
        val weather = Gson().fromJson(weatherJson, Weather::class.java)

        initToolBarAndFAB(weather)
    }

    override fun onDestroy() {
        super.onDestroy()

        //set
        ActivitySet.removeActivity(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }
        return true
    }

    private fun isDarkTheme(context: Context):Boolean{
        val flag = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return flag == Configuration.UI_MODE_NIGHT_YES
    }

    private fun initToolBarAndFAB(weather: Weather){
        //Title
        supportActionBar?.title = weather.minutely.description
        //Sub Title
        minutelyActivityToolBar.subtitle = "2 hours:" +
                (weather.minutely.precipitation_2h[0]*100).toString() + "%"
    }

    private fun initMinutelyRainMap(weather: Weather){

    }

    private fun initRainChart(weather: Weather){

    }
}
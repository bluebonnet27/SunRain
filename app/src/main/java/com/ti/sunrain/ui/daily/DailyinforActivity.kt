package com.ti.sunrain.ui.daily

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.ti.sunrain.R
import com.ti.sunrain.SunRainApplication
import com.ti.sunrain.databinding.ActivityDailyinforBinding
import com.ti.sunrain.logic.ActivitySet
import com.ti.sunrain.logic.model.*
//import kotlinx.android.synthetic.main.activity_dailyinfor.*
//import kotlinx.android.synthetic.main.item_daily_day.*
//import kotlinx.android.synthetic.main.item_daily_lifeindex.*
//import kotlinx.android.synthetic.main.item_daily_night.*
//import kotlinx.android.synthetic.main.item_daily_others.*
import java.text.SimpleDateFormat
import java.util.*


class DailyinforActivity : AppCompatActivity() {
    lateinit var activityDailyinforBinding: ActivityDailyinforBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDailyinforBinding = ActivityDailyinforBinding.inflate(layoutInflater)
        setContentView(activityDailyinforBinding.root)

        //set
        ActivitySet.addActivity(this)

        //darkmode
        when(SunRainApplication.settingsPreference.getString("others_darkmode_list","0")){
            "0" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            "1" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            "2" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        }

        setSupportActionBar(activityDailyinforBinding.dailyInforToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24dp)
        }

        val weatherJson = intent.getStringExtra("weather")
        val weather = Gson().fromJson(weatherJson,Weather::class.java)
        val dayIndex = intent.getIntExtra("dayIndex",0)
        val weekday = intent.getStringExtra("weekday")?:"ERROR"

        initToolBarAndFAB(weather,dayIndex,weekday)
        setWeatherDayAndNight(weather,dayIndex)
        setWeatherLifeIndex(weather,dayIndex)
        setOtherWeatherInformation(weather,dayIndex)
    }

    override fun onDestroy() {
        super.onDestroy()

        //set
        ActivitySet.removeActivity(this)
    }

    private fun initToolBarAndFAB(weather: Weather, index: Int,weekday:String){
        //date
        val dateOrigin = weather.daily.skyconDaylight[index].date
        val humidityOrigin = weather.daily.humidity[index].avgHumidity
        val simpleDateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
        supportActionBar?.title = simpleDateFormat.format(dateOrigin) + "($weekday)"

        //temperature
        val temperatureOrigin = weather.daily.temperature[index]
        val tMaxTxt = resources.getString(R.string.max_temp) + ":" + temperatureOrigin.max.toInt()
        val tMinTxt = resources.getString(R.string.min_temp) + ":" + temperatureOrigin.min.toInt()
        val humidityText = (humidityOrigin*100).toString()
        val tempText = "$tMinTxt℃   $tMaxTxt℃   湿度：$humidityText%"
        activityDailyinforBinding.dailyInforToolbar.subtitle = tempText

        //icon season
        val seasonIcon = getSeasonIcon(getSeason(dateOrigin))
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            activityDailyinforBinding.dailyFAB.setImageIcon(Icon.createWithResource(this,seasonIcon))
        }

        activityDailyinforBinding.dailyFAB.setOnClickListener { view ->
            when(getSeason(dateOrigin)){
                1 -> Snackbar.make(view,"她总会烦恼，总会忧伤",Snackbar.LENGTH_SHORT).show()
                2 -> Snackbar.make(view,"再美的语言，也需要景色的铺垫",Snackbar.LENGTH_SHORT).show()
                3 -> Snackbar.make(view,"快乐渺无痕迹",Snackbar.LENGTH_SHORT).show()
                4 -> Snackbar.make(view,"如果我忍住这个秘密",Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setWeatherDayAndNight(weather: Weather,index:Int){
        //day,night coord information
        //day-wind
        val windSpeed = weather.daily.wind[index].avgWind.speed
        val windDirection = weather.daily.wind[index].avgWind.direction
        //day-skycon
        val skyconDay = getSky(weather.daily.skyconDaylight[index].value)

        activityDailyinforBinding.itemDailyDayInclude.let {
            it.daySkyconIcon.setImageResource(skyconDay.weather_icon)
            it.daySkyconText.text = skyconDay.info

            it.dayWindIcon.setImageResource(getWindIcon(getWindSpeed(windSpeed)))
            it.dayWindDirection.text = String.format(resources.getString(R.string.dailyinforActivity_dayWindDirection_text), getWindDirection(windDirection))
            it.dayWindSpeed.text = String.format(resources.getString(R.string.dailyinforActivity_dayWindSpeed_text), getWindSpeed(windSpeed))
        }

        //night-skycon
        val skyconNight = getSky(weather.daily.skyconNight[index].value)

        activityDailyinforBinding.itemDailyNightInclude.let {
            it.nightSkyconIcon.setImageResource(skyconNight.weather_icon)
            it.nightSkyconText.text = skyconNight.info

            it.nightWindIcon.setImageResource(getWindIcon(getWindSpeed(windSpeed)))
            it.nightWindDirection.text = String.format(resources.getString(R.string.dailyinforActivity_dayWindDirection_text), getWindDirection(windDirection))
            it.nightWindSpeed.text = String.format(resources.getString(R.string.dailyinforActivity_dayWindSpeed_text), getWindSpeed(windSpeed))
        }

        //darkTheme
        if(isDarkTheme(this)){
            activityDailyinforBinding.itemDailyDayInclude.dayInfoImg.setImageResource(R.drawable.baseline_wb_sunny_white_24dp)
            activityDailyinforBinding.itemDailyNightInclude.nightInforImg.setImageResource(R.drawable.baseline_nights_stay_white_24dp)
        }
    }

    private fun setWeatherLifeIndex(weather: Weather,index: Int){
        //life index common
        val ultravioletDesc = weather.daily.lifeIndex.ultraviolet[index].desc
        val carWashingDesc = weather.daily.lifeIndex.carWashing[index].desc
        val coldRiskDesc = weather.daily.lifeIndex.coldRisk[index].desc
        val dressingDesc = weather.daily.lifeIndex.dressing[index].desc

        //life index air
        val aqiValue = weather.daily.aqi.aqiList[index].avg.chn.toString()
        val aqiDesc = "空气" + getAQIDesc(weather.daily.aqi.aqiList[index].avg.chn.toInt())
        val pm25Desc = weather.daily.aqi.pm25List[index].avg.toString()

        activityDailyinforBinding.itemDailyLifeIndexInclude.let {
            it.ultravioletTextItem.text = ultravioletDesc
            it.carWashingTextItem.text = carWashingDesc
            it.coldRiskTextItem.text = coldRiskDesc
            it.dressingTextItem.text = dressingDesc

            it.aqiTextTitleItem.text = aqiValue
            it.aqiTextContextItem.text = aqiDesc
            it.pm25TextItem.text = pm25Desc
        }

        //darkmode
        if(isDarkTheme(this)){
            activityDailyinforBinding.itemDailyLifeIndexInclude.lifeIndexInfoImg.setImageResource(R.drawable.baseline_nature_people_white_24dp)
        }
    }

    private fun setOtherWeatherInformation(weather: Weather,index:Int){
        //rain
        val rainPrecipitation = weather.daily.precipitation[index].avg.toString()
        activityDailyinforBinding.itemDailyOthersInclude.rainDailyText.text = String.format(
            resources.getString(R.string.dailyinforActivity_rainDailyText_text),rainPrecipitation)
        //sun Rise&Set
        val sunRiseTime = weather.daily.astro[index].sunrise.risetime
        val sunSetTime = weather.daily.astro[index].sunset.settime

        activityDailyinforBinding.itemDailyOthersInclude.sunriseTimeText.text = sunRiseTime
        activityDailyinforBinding.itemDailyOthersInclude.sunsetTimeText.text = sunSetTime

        //darkmode
        if(isDarkTheme(this)){
            activityDailyinforBinding.itemDailyOthersInclude.let {
                it.othersInfoImg.setImageResource(R.drawable.baseline_filter_vintage_white_24dp)
                it.rainDailyIcon.setImageResource(R.drawable.baseline_beach_access_white_24dp)
                it.astroDailyIcon.setImageResource(R.drawable.baseline_brightness_medium_white_24dp)
                it.sunriseIcon.setImageResource(R.drawable.ic_sunrise_material_white_48px)
                it.sunsetIcon.setImageResource(R.drawable.ic_sunset_material_white_48px)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }
        return true
    }

    private fun getSeason(date: Date):Int{
        val springNum = 1
        val summerNum = 2
        val autumnNum = 3
        val winterNum = 4

        val seasonErrorNum = 0

        val calendar = Calendar.getInstance()
        calendar.time = date
        return when (calendar.get(Calendar.MONTH)) {
            in 0..1 -> {
                winterNum
            }
            in 2..4 -> {
                springNum
            }
            in 5..7 -> {
                summerNum
            }
            in 8..10 -> {
                autumnNum
            }
            in 11..12 -> {
                winterNum
            }
            else -> {
                seasonErrorNum
            }
        }
    }

    private fun getSeasonIcon(season:Int) = when(season){
            1 -> R.drawable.ic_spring_color
            2 -> R.drawable.ic_summer_color
            3 -> R.drawable.ic_autumn_color
            4 -> R.drawable.ic_winter_color
            else -> R.drawable.ic_spring_color
    }

    private fun getAQIDesc(aqiValue:Int):String{
        when (aqiValue) {
            in 0..50 -> {
                return "优"
            }
            in 51..100 -> {
                return "良"
            }
            in 101..150 -> {
                return "轻度污染"
            }
            in 151..200 -> {
                return "中度污染"
            }
            in 201..300 -> {
                return "重度污染"
            }
            else -> {
                return "严重污染"
            }
        }
    }

    private fun isDarkTheme(context: Context):Boolean{
        val flag = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return flag == Configuration.UI_MODE_NIGHT_YES
    }
}
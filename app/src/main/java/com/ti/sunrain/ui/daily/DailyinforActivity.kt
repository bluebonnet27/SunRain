package com.ti.sunrain.ui.daily

import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import android.view.MenuItem
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.ti.sunrain.R
import com.ti.sunrain.SunRainApplication
import com.ti.sunrain.logic.ActivitySet
import com.ti.sunrain.logic.model.*
import kotlinx.android.synthetic.main.activity_dailyinfor.*
import kotlinx.android.synthetic.main.item_daily_day.*
import kotlinx.android.synthetic.main.item_daily_lifeindex.*
import kotlinx.android.synthetic.main.item_daily_night.*
import kotlinx.android.synthetic.main.item_daily_others.*
import java.text.SimpleDateFormat
import java.util.*


class DailyinforActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        window.apply {
//            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
//            enterTransition = Fade()
//            exitTransition = androidx.transition.Fade()
//        }
        setContentView(R.layout.activity_dailyinfor)

        //set
        ActivitySet.addActivity(this)

        //darkmode
        when(SunRainApplication.settingsPreference.getString("others_darkmode_list","0")){
            "0" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            "1" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            "2" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        }

        setSupportActionBar(dailyInforToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24dp)
        }

        val weatherJson = intent.getStringExtra("weather")
        val weather = Gson().fromJson(weatherJson,Weather::class.java)
        val dayIndex = intent.getIntExtra("dayIndex",0)

        initToolBarAndFAB(weather,dayIndex)
        setWeatherDayAndNight(weather,dayIndex)
        setWeatherLifeIndex(weather,dayIndex)
        setOtherWeatherInformation(weather,dayIndex)
    }

    override fun onDestroy() {
        super.onDestroy()

        //set
        ActivitySet.removeActivity(this)
    }

    private fun initToolBarAndFAB(weather: Weather, index: Int){
        //date
        val dateOrigin = weather.daily.skyconDaylight[index].date
        val simpleDateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
        supportActionBar?.title = simpleDateFormat.format(dateOrigin) + "(${getDayDesc(index)})"

        //temperature
        val temperatureOrigin = weather.daily.temperature[index]
        val tMaxTxt = resources.getString(R.string.max_temp) + ":" + temperatureOrigin.max.toInt()
        val tMinTxt = resources.getString(R.string.min_temp) + ":" + temperatureOrigin.min.toInt()
        val tempText = "$tMinTxt℃   $tMaxTxt℃"
        dailyInforToolbar.subtitle = tempText

        //icon season
        val seasonIcon = getSeasonIcon(getSeason(dateOrigin))
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            dailyFAB.setImageIcon(Icon.createWithResource(this,seasonIcon))
        }

        dailyFAB.setOnClickListener { view ->
            when(getSeason(dateOrigin)){
                1 -> Snackbar.make(view,"她总会烦恼，总会忧伤",Snackbar.LENGTH_SHORT).show()
                2 -> Snackbar.make(view,"再美的语言，也需要景色的铺垫",Snackbar.LENGTH_SHORT).show()
                3 -> Snackbar.make(view,"快乐渺无痕迹",Snackbar.LENGTH_SHORT).show()
                4 -> Snackbar.make(view,"如果我忍住这个秘密",Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setWeatherDayAndNight(weather: Weather,index:Int){
        //day-skycon
        val skyconDay = getSky(weather.daily.skyconDaylight[index].value)
        daySkyconIcon.setImageResource(skyconDay.weather_icon)
        daySkyconText.text = skyconDay.info

        //day-wind
        val windSpeed = weather.daily.wind[index].avgWind.speed
        val windDirection = weather.daily.wind[index].avgWind.direction

        dayWindIcon.setImageResource(getWindIcon(getWindSpeed(windSpeed)))
        dayWindDirection.text = "风向: ${getWindDirection(windDirection)}风"
        dayWindSpeed.text = "风速: ${getWindSpeed(windSpeed)}级"

        //night-skycon
        val skyconNight = getSky(weather.daily.skyconNight[index].value)
        nightSkyconIcon.setImageResource(skyconNight.weather_icon)
        nightSkyconText.text = skyconNight.info

        //night-wind
        //先将就着用白天的数据吧，等以后有钱了买付费源就换成正式的夜间天气
        nightWindIcon.setImageResource(getWindIcon(getWindSpeed(windSpeed)))
        nightWindDirection.text = "风向: ${getWindDirection(windDirection)}风"
        nightWindSpeed.text = "风速: ${getWindSpeed(windSpeed)}级"
    }

    private fun setWeatherLifeIndex(weather: Weather,index: Int){
        //life index
        val ultravioletDesc = weather.daily.lifeIndex.ultraviolet[index].desc
        val carWashingDesc = weather.daily.lifeIndex.carWashing[index].desc
        val coldRiskDesc = weather.daily.lifeIndex.coldRisk[index].desc
        val dressingDesc = weather.daily.lifeIndex.dressing[index].desc

        ultravioletTextItem.text = ultravioletDesc
        carWashingTextItem.text = carWashingDesc
        coldRiskTextItem.text = coldRiskDesc
        dressingTextItem.text = dressingDesc
    }

    private fun setOtherWeatherInformation(weather: Weather,index:Int){
        //rain
        val rainPrecipitation = weather.daily.precipitation[index].avg.toString()
        rainDailyText.text = rainPrecipitation + "mm/h"
        //sun Rise&Set
        val sunRiseTime = weather.daily.astro[index].sunrise.risetime
        val sunSetTime = weather.daily.astro[index].sunset.settime

        sunriseTimeText.text = sunRiseTime
        sunsetTimeText.text = sunSetTime
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }
        return true
    }

    private fun getDayDesc(index:Int):String{
        return when(index){
            0 -> resources.getString(R.string.today)
            1 -> resources.getString(R.string.tomorrow)
            2 -> resources.getString(R.string.day_after_tomorrow)
            3 -> resources.getString(R.string.day_2after_tomorrow)
            4 -> resources.getString(R.string.day_3after_tomorrow)
            else -> "ERROR"
        }
    }

    fun getSeason(date: Date):Int{
        val SPRING = 1
        val SUMMER = 2
        val AUTUMN = 3
        val WINTER = 4

        val calendar = Calendar.getInstance()
        calendar.time = date
        val month = calendar.get(Calendar.MONTH)
        return if(month in 0..1){
            WINTER
        }else if(month in 2..4){
            SPRING
        }else if(month in 5..7){
            SUMMER
        }else if(month in 8..10){
            AUTUMN
        }else if(month in 11..12){
            WINTER
        }else{
            0
        }
    }

    fun getSeasonIcon(season:Int) = when(season){
            1 -> R.drawable.ic_spring_color
            2 -> R.drawable.ic_summer_color
            3 -> R.drawable.ic_autumn_color
            4 -> R.drawable.ic_winter_color
            else -> R.drawable.ic_spring_color
    }

    fun getSeasonColor(season: Int) = when(season){
        1 -> 0x388e3c
        2 -> 0xffa000
        3 -> 0x5d4037
        4 -> 0x455a64
        else -> 0xd32f2f
    }
}
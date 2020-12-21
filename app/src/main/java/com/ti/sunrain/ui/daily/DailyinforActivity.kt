package com.ti.sunrain.ui.daily

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.gson.Gson
import com.ti.sunrain.R
import com.ti.sunrain.logic.ActivitySet
import com.ti.sunrain.logic.model.*
import kotlinx.android.synthetic.main.activity_dailyinfor.*
import kotlinx.android.synthetic.main.item_daily_day.*
import kotlinx.android.synthetic.main.item_daily_night.*
import java.text.SimpleDateFormat
import java.util.*

class DailyinforActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dailyinfor)

        //set
        ActivitySet.addActivity(this)

        setSupportActionBar(dailyInforToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24dp)
        }

        val weatherJson = intent.getStringExtra("weather")
        val weather = Gson().fromJson(weatherJson,Weather::class.java)
        val dayIndex = intent.getIntExtra("dayIndex",0)

        initToolBar(weather,dayIndex)
        setWeatherDayAndNight(weather,dayIndex)
    }

    override fun onDestroy() {
        super.onDestroy()

        //set
        ActivitySet.removeActivity(this)
    }

    private fun initToolBar(weather: Weather,index: Int){
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
        dayWindDirection.text = "Wind direction: ${getWindDirection(windDirection)} wind"
        dayWindSpeed.text = "Wind speed: ${getWindSpeed(windSpeed)} wind"

        //night-skycon
        val skyconNight = getSky(weather.daily.skyconNight[index].value)
        nightSkyconIcon.setImageResource(skyconNight.weather_icon)
        nightSkyconText.text = skyconNight.info

        //night-wind
        //先将就着用白天的数据吧，等以后有钱了买付费源就换成正式的夜间天气
        nightWindIcon.setImageResource(getWindIcon(getWindSpeed(windSpeed)))
        nightWindDirection.text = "Wind direction: ${getWindDirection(windDirection)} wind"
        nightWindSpeed.text = "Wind speed: ${getWindSpeed(windSpeed)} wind"
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
        if(month in 0..1){
            return WINTER
        }else if(month in 2..4){
            return SPRING
        }else if(month in 5..7){
            return SUMMER
        }else if(month in 8..10){
            return AUTUMN
        }else if(month in 11..12){
            return WINTER
        }else{
            return 0
        }
    }
}
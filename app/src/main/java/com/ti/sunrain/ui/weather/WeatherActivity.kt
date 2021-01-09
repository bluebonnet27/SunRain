package com.ti.sunrain.ui.weather

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.audiofx.BassBoost
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.NotificationCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.ti.sunrain.ui.covid.CovidSpecial
import com.ti.sunrain.R
import com.ti.sunrain.SunRainApplication
import com.ti.sunrain.logic.ActivitySet
import com.ti.sunrain.logic.model.*
import com.ti.sunrain.ui.about.AboutActivity
import com.ti.sunrain.ui.daily.DailyinforActivity
import com.ti.sunrain.ui.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.air.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.forecast_chart.*
import kotlinx.android.synthetic.main.hourly.*
import kotlinx.android.synthetic.main.item_progress_sun.*
import kotlinx.android.synthetic.main.life_index.*
import kotlinx.android.synthetic.main.now.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WeatherActivity : AppCompatActivity() {

    //2020-12-14 重构代码，UI层优于逻辑层，重写函数优于自定义函数
    //懒加载 viewmodel
    val viewModel by lazy { ViewModelProviders.of(this).get(WeatherViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        //set
        ActivitySet.addActivity(this)

        //darkmode
        when(SunRainApplication.settingsPreference.getString("others_darkmode_list","0")){
            "0" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            "1" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            "2" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        }

        setSupportActionBar(weatherToolBar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.baseline_location_city_white_24dp)
        }

        drawerLayout.addDrawerListener(object:DrawerLayout.DrawerListener{
            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
            }
        })

        if(viewModel.locationLng.isEmpty()){
            viewModel.locationLng = intent.getStringExtra("location_lng")?:""
        }

        if(viewModel.locationLat.isEmpty()){
            viewModel.locationLat = intent.getStringExtra("location_lat")?:""
        }

        if(viewModel.placeName.isEmpty()){
            viewModel.placeName = intent.getStringExtra("place_name")?:""
        }

        //live data
        viewModel.weatherLiveData.observe(this, Observer{ result->
            val weather = result.getOrNull()
            if(weather!=null){
                showWeatherInfo(weather)
            }else{
                Toast.makeText(this, "无法获取天气信息啦！", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false
        })

        swipeRefresh.setColorSchemeResources(R.color.blue500)
        refreshWeather()
        swipeRefresh.setOnRefreshListener {
            refreshWeather()
            Snackbar.make(swipeRefresh,"已发送刷新",Snackbar.LENGTH_SHORT).show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        //set
        ActivitySet.removeActivity(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)

            R.id.COVIDExplorer ->{
                val covidIntent = Intent(this,
                    CovidSpecial::class.java)
                startActivity(covidIntent)
            }
            R.id.settingsIcon -> {
                val settingsIntent = Intent(this,SettingsActivity::class.java)
                startActivity(settingsIntent)
            }
            R.id.aboutIcon -> {
                val aboutIntent = Intent(this,AboutActivity::class.java)
                startActivity(aboutIntent)
            }
            R.id.shareIcon -> {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "我正在使用晴雨，这个APP很好用，可以看天气预报，\n" +
                            "它的下载地址是：https://bluebonnet27.gitee.io/")
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, "把下载网址发给")
                startActivity(shareIntent)
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu,menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        //COVID
        val covidOption = menu?.getItem(0)
        covidOption?.isVisible = SunRainApplication.settingsPreference.getBoolean("covid19_switch",true)
        return super.onPrepareOptionsMenu(menu)
    }

    /**
     * 刷新天气，利用经纬度
     */
    fun refreshWeather(){
        viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)
        swipeRefresh.isRefreshing = true
    }

    /**
     * 主要的刷新天气函数
     */
    private fun showWeatherInfo(weather: Weather){

        //天气通知频道
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel("sun_rain_realtime","即时天气",
                NotificationManager.IMPORTANCE_LOW)
            manager.createNotificationChannel(channel)
        }

        //获取ViewModel中的数据
        val realtime = weather.realtime
        val daily = weather.daily
        val responseForRealtime = weather.realtimeResponse
        val windReturn = weather.wind
        val hourlyReturn = weather.hourly
        val serverTime = responseForRealtime.getServerTime()
        val serverTimeText = viewModel.changeUNIXIntoString(serverTime)

        //ToolBar 数据
        initToolBarBasic(viewModel.placeName,"$serverTimeText ${resources.getString(R.string.refresh)}")

        //now.xml 数据
        val currentTempText = "${realtime.temperature.toInt()}°"
        currentTemperature.text = currentTempText
        currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = resources.getString(R.string.airIndex)+" ${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text
        val currentAQIDescInfor = realtime.airQuality.description.chn
        currentAQIDesc.text = " $currentAQIDescInfor"

        //now.xml 动态背景
        val festivalBackgroundValue = SunRainApplication.settingsPreference.getBoolean("festival_bg_switch",true)
        val originBackground = (getSky(realtime.skycon).bg)
        if(festivalBackgroundValue){
            drawerLayout.setBackgroundResource(changeBackgroundByHours(originBackground,judgeTimeInDay()))
        }
        else{
            drawerLayout.setBackgroundResource(originBackground)
        }

        //progressbar 数据
        initProgressBar(daily,serverTimeText)

        //forecast.xml 数据
        forecastDesc.text = "此刻天气" + weather.realtime.lifeIndex.comfort.desc + " 紫外线" + weather.realtime.lifeIndex.ultraviolet.desc
        forecastLayout.removeAllViews()
        val days = daily.skyconSum.size

        val listHigh = ArrayList<Entry>()
        val listLow = ArrayList<Entry>()

        val dateFormatValue = SunRainApplication.settingsPreference.getString("forecastDateFormat_list","0")

        for(i in 0 until days){
            val skycon = daily.skyconSum[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                forecastLayout,false)

            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById<TextView>(R.id.temperatureInfo)

            if(dateFormatValue=="0"){
                dateInfo.text = getDayDesc(i)
            }else{
                val dateOrigin = skycon.date
                val simpleDateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
                dateInfo.text = simpleDateFormat.format(dateOrigin)
            }

            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.weather_icon)
            skyInfo.text = sky.info

            listHigh.add(Entry(i.toFloat(),temperature.max))
            listLow.add(Entry(i.toFloat(),temperature.min))

            val tempText = "${temperature.min.toInt()}~${temperature.max.toInt()}℃"
            temperatureInfo.text = tempText

            view.setOnClickListener {
                val dailyInfoIntent = Intent(this,DailyinforActivity::class.java)
                dailyInfoIntent.putExtra("weather",Gson().toJson(weather))
                dailyInfoIntent.putExtra("dayIndex",i)
                startActivity(dailyInfoIntent)
            }

            forecastLayout.addView(view)
        }

        //天气通知正文
        val rTnotification = showRealtimeWeatherNotification(weather)
        if(!SunRainApplication.settingsPreference.getBoolean("notification_cancancel_switch",false)){
            rTnotification.flags = Notification.FLAG_NO_CLEAR
        }
        
        if(SunRainApplication.settingsPreference.getBoolean("notification_switch",false)){
            manager.notify(1,rTnotification)
        }else{
            manager.cancel(1)
        }

        //temperatureChart 数据
        val isForecastChartVisible = SunRainApplication.settingsPreference.getBoolean("forecast_chart_switch",false)
        if(isForecastChartVisible){
            tempChartCard.visibility = VISIBLE
        }else{
            tempChartCard.visibility = GONE
        }

        val setHigh = LineDataSet(listHigh,"High")
        setHigh.mode = LineDataSet.Mode.CUBIC_BEZIER
        setHigh.color = Color.parseColor("#D50000")

        val setLow = LineDataSet(listLow,"Low")
        setLow.mode = LineDataSet.Mode.CUBIC_BEZIER
        setLow.color = Color.parseColor("#0091EA")

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(setHigh)
        dataSets.add(setLow)
        val dataTemp = LineData(dataSets)

        temperatureChart.apply {
            setTouchEnabled(false)
            axisLeft.setDrawAxisLine(false)
            description.isEnabled = false
        }

        val xAxis = temperatureChart.xAxis
        xAxis.setDrawAxisLine(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE

        temperatureChart.data = dataTemp

        //lifeindex.xml 数据注入
        val lifeIndex = daily.lifeIndex
        coldRiskText.text = lifeIndex.coldRisk[0].desc //今天数据就是一组数据里的第一个
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        comfortText.text = lifeIndex.comfort[0].desc
        windIndexTextDirection.text = "${getWindDirection(windReturn.direction)}风"
        windIndexTextLevel.text = "风力${getWindSpeed(windReturn.speed)}级"

        //lifeindex.xml CLick
        ColdRiskItem.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cold Risk")
                .setMessage("明天：  ${lifeIndex.coldRisk[1].desc}\n"+
                            "后天：  ${lifeIndex.coldRisk[2].desc}\n"+
                            "大后天：${lifeIndex.coldRisk[3].desc}")
                .show()
        }

        weatherLayout.visibility = View.VISIBLE

        //air.xml 数列写入数据
        airDesc.text = weather.realtime.airQuality.description.chn

        val aq = realtime.airQuality
        pm25Num.text = "${aq.pm25} μg/m3"
        pm10Num.text = "${aq.pm10} μg/m3"
        no2Num.text = "${aq.no2} μg/m3"
        o3Num.text = "${aq.o3} μg/m3"
        so2Num.text = "${aq.so2} μg/m3"
        coNum.text = "${aq.co} mg/m3"

        //air.xml 图绘制
        val dirtyData = ArrayList<PieEntry>()
        dirtyData.add(PieEntry(aq.aqi.chn,""))
        dirtyData.add(PieEntry(600-aq.aqi.chn,""))
        val dirtyDataSet = PieDataSet(dirtyData,"")

        //air.xml 美化
        dirtyDataSet.setColors(Color.parseColor(viewModel.getAQIColor(aq.aqi.chn.toInt())),
                                Color.parseColor("#eeeeee"))
        dirtyDataSet.valueTextSize = 0f
        airPie.holeRadius = 90f
        airPie.description.isEnabled = false
        airPie.transparentCircleRadius = 0f
        airPie.centerText = aq.aqi.chn.toInt().toString()
        airPie.setCenterTextSize(18f)

        if(isDarkTheme(SunRainApplication.context)){
            airPie.setCenterTextColor(Color.WHITE)
            airPie.setHoleColor(Color.parseColor("#ff3f3f3f"))
        }else{
            airPie.setCenterTextColor(Color.BLACK)
            airPie.setHoleColor(Color.WHITE)
        }

        airPie.animateXY(4000,4000)
        airPie.legend.isEnabled = false

        val dirtyDataUse = PieData(dirtyDataSet)
        airPie.data = dirtyDataUse

        //hourly 数据
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        hourlyLayout.layoutManager = layoutManager
        val adapter = HourlyAdapter(initHourlyItemList(hourlyReturn))
        hourlyLayout.adapter = adapter

        hourlyDescText.text = hourlyReturn.description
    }

    /**
     * 初始化主 toolbar 的标题和副标题
     */
    private fun initToolBarBasic(title: String, subTitle:String){
        weatherToolBar.title = title
        weatherToolBar.subtitle = subTitle
    }

    /**
     * 判断是否是深色模式
     */
    private fun isDarkTheme(context:Context):Boolean{
        val flag = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return flag == Configuration.UI_MODE_NIGHT_YES
    }

    /**
     * index转换为日期描述，仓库层缺少resources引用所以就直接复制过来了
     */
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

    /**
     * 填充小时天气的数据
     */
    private fun initHourlyItemList(hourly:HourlyResponse.Result.Hourly):ArrayList<HourlyItem>{
        val hourlyItems = ArrayList<HourlyItem>(hourly.temperature.size)
        for(i in hourly.temperature.indices){
            val skycon = hourly.skycon[i]
            val temperature = hourly.temperature[i]
            val wind = hourly.wind[i]

            hourlyItems.add(HourlyItem(skycon,temperature,wind))
        }
        return hourlyItems
    }

    /**
     * 天气通知
     */
    private fun showRealtimeWeatherNotification(weather: Weather):Notification{
        val skyConToday = getSky(weather.daily.skyconSum[0].value)

        var icon_day_or_night = R.drawable.baseline_wb_sunny_black_24dp

        if(isDarkTheme(this)){
            icon_day_or_night = R.drawable.baseline_nights_stay_white_24dp
        }

        val notification =  NotificationCompat.Builder(this,"sun_rain_realtime")
            .setContentTitle("${weather.realtime.temperature.toInt()}°${getSky(weather.realtime.skycon).info}")
            .setSmallIcon(icon_day_or_night)
            .setLargeIcon(BitmapFactory.decodeResource(resources,skyConToday.weather_icon))

        //val preferencesNotificationLong = getSharedPreferences("settings",0)
        val isMoreinfo = SunRainApplication.settingsPreference.getBoolean("notification_moreinfo_switch",false)

        if(isMoreinfo){
            notification.setStyle(NotificationCompat.BigTextStyle().bigText("AQI:${weather.realtime.airQuality.aqi.chn}\n" +
                    weather.hourly.description
            ))
        }else{
            notification.setContentText("AQI:${weather.realtime.airQuality.aqi.chn} ${weather.realtime.airQuality.description.chn}")
        }

        return notification.build()
    }

    /**
     * 利用三个时间点得到进度条进度 x/100
     */
    private fun getSunProgressFromTimes(sunRiseTime:String,sunSetTime:String,current:String):Int{
        val timeFormatter = SimpleDateFormat("HH:mm",Locale.getDefault())

        val sunRise = timeFormatter.parse(sunRiseTime)
        val sunSet = timeFormatter.parse(sunSetTime)
        val currentTime = timeFormatter.parse(current)

        if(sunRise == null || sunSet == null || currentTime == null){
            return 0
        }

        val setSubRise = (sunSet.time - sunRise.time).toDouble()
        val currentSubRise = (currentTime.time - sunRise.time).toDouble()

        return ((currentSubRise/setSubRise)*100).toInt()
    }

    /**
     * 初始化日升日落进度条
     */
    private fun initProgressBar(daily: DailyResponse.Daily,serverTimeText:String){
        //SunRise ProgressBar
        val sunRiseTime = daily.astro[0].sunrise.risetime
        val sunSetTime = daily.astro[0].sunset.settime

        sunRiseTimeProgress.text = "↑$sunRiseTime"
        sunSetTimeProgress.text = "$sunSetTime↓"

        val result = getSunProgressFromTimes(sunRiseTime,sunSetTime,serverTimeText)
        sunTimeProgressBar.progress = result
    }

    private fun judgeTimeInDay():Int{
        val date = Date()
        val df = SimpleDateFormat("HH", Locale.getDefault())
        val hours = Integer.parseInt(df.format(date))

        return if(hours in 0..5){
            0
        }else if(hours in 6..11){
            1
        }else if(hours == 12){
            3
        }else if(hours in 13..18){
            4
        }else{
            5
        }
    }

    private fun changeBackgroundByHours(originBackgroundResId:Int,hour:Int):Int{
        return if(hour == 4){
            if(originBackgroundResId == R.drawable.bg_sunnyday_new){
                R.drawable.bg_sunnyafternoon_sp
            }else{
                originBackgroundResId
            }
        }else{
            originBackgroundResId
        }
    }
}
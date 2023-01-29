package com.ti.sunrain.ui.weather

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.permissionx.guolindev.PermissionX
import com.ti.sunrain.BuildConfig
import com.ti.sunrain.R
import com.ti.sunrain.SunRainApplication
import com.ti.sunrain.databinding.ActivityWeatherBinding
import com.ti.sunrain.logic.ActivitySet
import com.ti.sunrain.logic.model.*
import com.ti.sunrain.logic.model.dayforecast.DayForecastItem
import com.ti.sunrain.ui.about.AboutActivity
import com.ti.sunrain.ui.air.AirActivity
import com.ti.sunrain.ui.covid.CovidSpecialActivity
import com.ti.sunrain.ui.daily.DailyinforActivity
import com.ti.sunrain.ui.futuredaily.FutureDailyActivity
import com.ti.sunrain.ui.minutely.MinutelyActivity
import com.ti.sunrain.ui.settings.SettingsActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WeatherActivity : AppCompatActivity() {

    //2020-12-14 重构代码，UI层优于逻辑层，重写函数优于自定义函数
    //懒加载 viewmodel
    val viewModel by lazy { ViewModelProvider(this)[WeatherViewModel::class.java] }
    lateinit var activityWeatherBinding: ActivityWeatherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityWeatherBinding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(activityWeatherBinding.root)

        //set
        ActivitySet.addActivity(this)

        //darkmode
        when(SunRainApplication.settingsPreference.getString("others_darkmode_list","0")){
            "0" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            "1" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            "2" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        }

        //welcome
        val welcomeData = SunRainApplication.settingsPreference.getBoolean("welcome",true)
        val welcomeDialogTitle = "隐私政策"
        val welcomeDialogMessage = "1. 当您使用晴雨时需要提供网络权限，用于将您输入的城市信息使用彩云天气的" +
                "第三方接口搜索天气。晴雨会将上一次搜索的记录保存在本地。通过清除软件数据，可" +
                "以将已保存的数据清除。晴雨不需要其他任何权限。\n\n"+"2. 当您选择使用手机定位功能" +
                "获取天气时，晴雨需要将从手机获取的定位数据发送至百度地图获取反解析地址。定位并非" +
                "唯一获取天气信息的方式，拒绝定位权限不会影响其他获取天气信息的功能\n\n"+
                "3. 如果您对您的隐私有任何疑问或者需要解释的，请通过产品中的反馈方式与开发者" +
                "取得联系。 如您不同意本协议或其中的任何条款的，您应停止使用晴雨。\n\n"+
                "4. 最后更新版本 V${BuildConfig.VERSION_NAME}"

        if(welcomeData){
            AlertDialog.Builder(this)
                .setTitle(welcomeDialogTitle)
                .setMessage(welcomeDialogMessage)
                .setPositiveButton(getString(R.string.ok)){_,_->run{
                        val settingsEditor = SunRainApplication.settingsPreference.edit()
                        settingsEditor.putBoolean("welcome",false)
                        settingsEditor.apply()

                        Toast.makeText(this, "您可以在关于页再次查看", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("退出"){
                    _,_->run{
                        ActivitySet.finishAllActivities()
                    }
                }
                .show()
        }

        setSupportActionBar(activityWeatherBinding.weatherToolBar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.baseline_location_city_white_24dp)
        }

        activityWeatherBinding.drawerLayout.addDrawerListener(object:DrawerLayout.DrawerListener{
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
            if (intent.getStringExtra("place_name")!=null){
                viewModel.placeName = intent.getStringExtra("place_name")!!
            }else{
                viewModel.placeName = "intent null"
            }
        }

        //live data
        viewModel.weatherLiveData.observe(this) { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法获取天气信息啦！", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            activityWeatherBinding.swipeRefresh.isRefreshing = false
        }

        activityWeatherBinding.swipeRefresh.setColorSchemeResources(R.color.blue500)
        refreshWeather()
        activityWeatherBinding.swipeRefresh.setOnRefreshListener {
            refreshWeather()
            Snackbar.make(activityWeatherBinding.swipeRefresh,"已发送刷新",Snackbar.LENGTH_SHORT).show()
        }

        if(SunRainApplication.settingsPreference.getBoolean("title_refresh_switch",false)){
            activityWeatherBinding.weatherToolBar.setOnClickListener {
                refreshWeather()
                Snackbar.make(activityWeatherBinding.swipeRefresh,"已发送刷新",Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        //set
        ActivitySet.removeActivity(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> activityWeatherBinding.drawerLayout.openDrawer(GravityCompat.START)

            R.id.COVIDExplorer ->{
                val covidIntent = Intent(this,
                    CovidSpecialActivity::class.java)
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
            R.id.exitIcon -> {
                Toast.makeText(this, "再见", Toast.LENGTH_SHORT).show()
                ActivitySet.finishAllActivities()
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
        covidOption?.isVisible = SunRainApplication.settingsPreference
            .getBoolean("covid19_switch",true)

        //dark theme
        if(isDarkTheme(this)){
            val settingsOption = menu?.getItem(1)
            settingsOption?.icon = ContextCompat
                .getDrawable(this,R.drawable.baseline_settings_white_24dp)
            val aboutOption = menu?.getItem(2)
            aboutOption?.icon = ContextCompat
                .getDrawable(this,R.drawable.baseline_sentiment_satisfied_alt_white_24dp)
            val shareOption = menu?.getItem(3)
            shareOption?.icon = ContextCompat
                .getDrawable(this,R.drawable.baseline_share_white_24dp)
            val exitOption = menu?.getItem(4)
            exitOption?.icon = ContextCompat
                .getDrawable(this,R.drawable.baseline_power_settings_new_white_24dp)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        if(SunRainApplication.settingsPreference.getBoolean("others_icon_menu",true)){
            if(menu.javaClass.simpleName.equals("MenuBuilder",false)){
                try {
                    val method = menu.javaClass.getDeclaredMethod("setOptionalIconsVisible",
                        Boolean::class.java)
                    method.isAccessible = true
                    method.invoke(menu,true)
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }
        return super.onMenuOpened(featureId, menu)
    }

    /**
     * 刷新天气，利用经纬度
     */
    fun refreshWeather(){
        viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)
        activityWeatherBinding.swipeRefresh.isRefreshing = true
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
        val minutelyReturn = weather.minutely
        val serverTime = responseForRealtime.getServerTime()
        val serverTimeText = viewModel.changeUNIXIntoString(serverTime)

        //ToolBar 数据
        initToolBarBasic(viewModel.placeName,"$serverTimeText ${resources.getString(R.string.refresh)}")

        //now.xml 数据
        activityWeatherBinding.nowInclude.let {
            val currentTempText = "${realtime.temperature.toInt()}°"
            it.currentTemperature.text = currentTempText
            it.currentSky.text = getSky(realtime.skycon).info
            val currentPM25Text = resources.getString(R.string.airIndex)+" ${realtime.airQuality.aqi.chn.toInt()}"
            it.currentAQI.text = currentPM25Text
            val currentAQIDescInfor = realtime.airQuality.description.chn
            it.currentAQIDesc.text = " $currentAQIDescInfor"

            //这行代码是测试天气动态背景的
            //nowAnimation.changeWeather(WeatherUtil.WeatherType.thunder)
            it.nowAnimation.changeWeather(getSkyAni(realtime.skycon))
            //middleRainy,heavyRainy,foggy,all snow,all rest
        }

        //now.xml 动态背景
        val festivalBackgroundValue = SunRainApplication.settingsPreference
            .getBoolean("festival_bg_switch",true)
        val originBackground = (getSky(realtime.skycon).bg)
        if(festivalBackgroundValue){
            activityWeatherBinding.drawerLayout.setBackgroundResource(changeBackgroundByHours(originBackground
                ,judgeTimeInDay()))
        }
        else{
            activityWeatherBinding.drawerLayout.setBackgroundResource(originBackground)
        }

        //progressbar 数据
        initProgressBar(daily,serverTimeText)

        //forecast.xml 数据
        activityWeatherBinding.forecastInclude.forecastDesc.text =
            "此刻天气" + weather.realtime.lifeIndex.comfort.desc +
                    " 紫外线" + weather.realtime.lifeIndex.ultraviolet.desc
        val days = daily.skyconSum.size

        val listHigh = ArrayList<Entry>()
        val listLow = ArrayList<Entry>()

        for(i in 0 until days){
            val temperature = daily.temperature[i]
            listHigh.add(Entry(i.toFloat(),temperature.max))
            listLow.add(Entry(i.toFloat(),temperature.min))
        }

        //新的预报recyclerview
        val dayForcastLayoutManager = LinearLayoutManager(this)
        dayForcastLayoutManager.orientation = LinearLayoutManager.VERTICAL
        activityWeatherBinding.forecastInclude.forecastRecyclerLayout.layoutManager =
            dayForcastLayoutManager

        val dayForecastAdapter = DayForecastAdapter(initDayForecastItems(daily))
        dayForecastAdapter.setOnItemClickListener(object : DayForecastAdapter.OnItemClickListener{
            override fun onItemClick(view: View, position: Int,weekday:String) {
                val dailyInfoIntent = Intent(this@WeatherActivity,DailyinforActivity::class.java)
                dailyInfoIntent.putExtra("weather",Gson().toJson(weather))
                dailyInfoIntent.putExtra("dayIndex",position)
                dailyInfoIntent.putExtra("weekday",weekday)
                startActivity(dailyInfoIntent)
            }
        })
        activityWeatherBinding.forecastInclude.forecastRecyclerLayout.adapter = dayForecastAdapter

        //未来十五天按钮
        activityWeatherBinding.forecastInclude.forecast15button.setOnClickListener {
            val futureDailyIntent = Intent(this,FutureDailyActivity::class.java)
            futureDailyIntent.putExtra("daily",Gson().toJson(daily))
            startActivity(futureDailyIntent)
        }

        //天气通知正文
        val sunrainWeatherNotification = showRealtimeWeatherNotification(weather)
        if(!SunRainApplication.settingsPreference.getBoolean("notification_cancancel_switch",false)){
            sunrainWeatherNotification.flags = Notification.FLAG_NO_CLEAR
        }
        
        if(SunRainApplication.settingsPreference.getBoolean("notification_switch",false)){
            PermissionX.init(this)
                .permissions(PermissionX.permission.POST_NOTIFICATIONS)
                .onExplainRequestReason{ scope, deniedList ->
                    val message = "通知权限用于发送天气通知，您已经在APP设置中打开通知开关，此处需要您授予以下权限"
                    scope.showRequestReasonDialog(deniedList, message, "同意", "拒绝")
                }
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        Toast.makeText(this, "天气通知发送成功", Toast.LENGTH_SHORT).show()
                        manager.notify(1,sunrainWeatherNotification)
                    } else {
                        Toast.makeText(this, "天气通知发送失败，因为您拒绝了如下权限：$deniedList", Toast.LENGTH_SHORT).show()
                        manager.cancel(1)
                    }
                }
        }else{
            manager.cancel(1)
        }

        //temperatureChart
        //可见性
        val isForecastChartVisible = SunRainApplication.settingsPreference
            .getBoolean("forecast_chart_switch",false)

        activityWeatherBinding.forecastChartInclude.tempChartCard.visibility =
            if(isForecastChartVisible) VISIBLE else GONE

        //最高温度
        val setHigh = LineDataSet(listHigh,"High")
        setHigh.mode = LineDataSet.Mode.CUBIC_BEZIER
        setHigh.color = Color.parseColor("#D50000")
        setHigh.valueTextSize = 12f
        setHigh.lineWidth = 3f

        //最低温度
        val setLow = LineDataSet(listLow,"Low")
        setLow.mode = LineDataSet.Mode.CUBIC_BEZIER
        setLow.color = Color.parseColor("#0091EA")
        setLow.valueTextSize = 12f
        setLow.lineWidth = 3f

        //添加数据
        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(setHigh)
        dataSets.add(setLow)
        val dataTemp = LineData(dataSets)

        activityWeatherBinding.forecastChartInclude.temperatureChart.let {
            it.data = dataTemp

            //折线图背景
            it.xAxis.setDrawGridLines(false)
            it.axisLeft.setDrawGridLines(false)

            //右下角英文字母
            it.description.isEnabled = false

            //图例
            it.legend.isEnabled = false

            //x轴
            it.xAxis.let {
                it.position = XAxis.XAxisPosition.BOTTOM
                // 加上这个就不会出现坐标点不对应的情况，但我不知道这是干啥的
                it.granularity = 1F
                it.valueFormatter = TemperatureChartXAxisFormatter()
            }

            //y轴
            it.axisRight.isEnabled = false
            it.axisLeft.isEnabled = false

            //不允许触摸
            it.setTouchEnabled(false)
        }

        //lifeindex.xml 数据注入
        val lifeIndex = daily.lifeIndex
        activityWeatherBinding.lifeIndexInclude.let {
            it.coldRiskText.text = lifeIndex.coldRisk[0].desc //今天数据就是一组数据里的第一个
            it.dressingText.text = lifeIndex.dressing[0].desc
            it.ultravioletText.text = lifeIndex.ultraviolet[0].desc
            it.carWashingText.text = lifeIndex.carWashing[0].desc
            it.comfortText.text = lifeIndex.comfort[0].desc
            it.windIndexTextDirection.text = "${getWindDirection(windReturn.direction)}风"
            it.windIndexTextLevel.text = "风力${getWindSpeed(windReturn.speed)}级"

            //lifeindex.xml CLick
            it.ColdRiskItem.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle(lifeIndex.coldRisk[0].desc)
                    .setMessage("明天的： ${lifeIndex.coldRisk[1].desc}\n"+
                            "后天的： ${lifeIndex.coldRisk[2].desc}\n"+
                            "大后天： ${lifeIndex.coldRisk[3].desc}")
                    .show()
            }

            it.dressingItem.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle(lifeIndex.dressing[0].desc)
                    .setMessage("明天的： ${lifeIndex.dressing[1].desc}\n"+
                            "后天的： ${lifeIndex.dressing[2].desc}\n"+
                            "大后天： ${lifeIndex.dressing[3].desc}")
                    .show()
            }

            it.ultravioletItem.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle(lifeIndex.ultraviolet[0].desc)
                    .setMessage("明天的： ${lifeIndex.ultraviolet[1].desc}\n"+
                            "后天的： ${lifeIndex.ultraviolet[2].desc}\n"+
                            "大后天： ${lifeIndex.ultraviolet[3].desc}")
                    .show()
            }

            it.carWashingItem.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle(lifeIndex.carWashing[0].desc)
                    .setMessage("明天的： ${lifeIndex.carWashing[1].desc}\n"+
                            "后天的： ${lifeIndex.carWashing[2].desc}\n"+
                            "大后天： ${lifeIndex.carWashing[3].desc}")
                    .show()
            }

            it.comfortItem.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle(lifeIndex.comfort[0].desc)
                    .setMessage("明天的： ${lifeIndex.comfort[1].desc}\n"+
                            "后天的： ${lifeIndex.comfort[2].desc}\n"+
                            "大后天： ${lifeIndex.comfort[3].desc}")
                    .show()
            }

            it.windDailyItem.setOnClickListener { view->
                Snackbar.make(view,"请前往对应日期页查看",Snackbar.LENGTH_SHORT).show()
            }
        }

        activityWeatherBinding.weatherLayout.visibility = VISIBLE

        //air.xml
        initAirHalf(weather)

        //空气页面
        activityWeatherBinding.halfItemAirInclude.airCard.setOnClickListener {
            val airActivityIntent = Intent(this,AirActivity::class.java)
            airActivityIntent.putExtra("weather",Gson().toJson(weather))
            startActivity(airActivityIntent)
        }

        //分钟降水页面
        activityWeatherBinding.halfItemMinutelyRainInclude.minutelyCard.setOnClickListener {
            val minutelyActivityIntent = Intent(this,MinutelyActivity::class.java)
            minutelyActivityIntent.putExtra("weather",Gson().toJson(weather))
            startActivity(minutelyActivityIntent)
        }

        //hourly 数据
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        activityWeatherBinding.hourlyInclude.let {
            it.hourlyLayout.layoutManager = layoutManager
            it.hourlyLayout.adapter = HourlyAdapter(initHourlyItemList(hourlyReturn))
            it.hourlyDescText.text = hourlyReturn.description
        }

        //分钟降水
        val minuteAdapter = MinutelyAdapter(initMinutelyItemList(minutelyReturn))

        //首页分钟降水
        activityWeatherBinding.halfItemMinutelyRainInclude.let {
            it.minutelyCardDescIconHS.setImageResource(transferData1MinuteToIcon(getRainData1Minute(minutelyReturn)))
            it.minutelyCardIcon.setImageResource(minuteAdapter.getRainIconDesc(getRainData1Minute(minutelyReturn)))
        }
    }

    /**
     * 初始化主 toolbar 的标题和副标题
     */
    private fun initToolBarBasic(title: String, subTitle:String){
        activityWeatherBinding.weatherToolBar.title = title
        activityWeatherBinding.weatherToolBar.subtitle = subTitle
        Log.d("WeatherActivity","TOOLBAR初始化成功，数据为$title,$subTitle")
    }

    /**
     * 判断是否是深色模式
     */
    private fun isDarkTheme(context:Context):Boolean{
        val flag = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return flag == Configuration.UI_MODE_NIGHT_YES
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
     * 填充分钟降水数据
     */
    private fun initMinutelyItemList(minutely:MinutelyResponse.Result.Minutely)
            :ArrayList<MinutelyItem>{
        val minutelyItems = ArrayList<MinutelyItem>(minutely.precipitation.size)
        //If you want to change the type of precipitation, please change the two params
        for(j in minutely.precipitation.indices){
            val minuteDescription = minutely.precipitation[j]

            minutelyItems.add(MinutelyItem(minuteDescription))
        }

        return minutelyItems
    }

    private fun getRainData1Minute(minutely: MinutelyResponse.Result.Minutely):Float{
        return minutely.precipitation[0]
    }

    private fun transferData1MinuteToIcon(precipitation:Float):Int {
        val isDarkTheme = isDarkTheme(this)
        val isPositive = (precipitation > 0)

        return if(isDarkTheme && isPositive){
            R.drawable.baseline_sentiment_dissatisfied_white_24dp
        }else if(!isDarkTheme && isPositive){
            R.drawable.baseline_sentiment_dissatisfied_black_24dp
        }else if(isDarkTheme && !isPositive){
            R.drawable.baseline_sentiment_satisfied_alt_white_24dp
        }else{
            R.drawable.baseline_sentiment_satisfied_alt_black_24dp
        }
    }

    /**
     * 初始化天气half
     */
    private fun initAirHalf(weather: Weather){
        activityWeatherBinding.halfItemAirInclude.let {
            it.airDescTextHalf.text = weather.realtime.airQuality.description.chn

            val aq = weather.realtime.airQuality
            it.airDescHalfAQI.text = aq.aqi.chn.toInt().toString()
            it.airDescHalfAQIProgress.progress = aq.aqi.chn.toInt()
        }
    }

    /**
     * 天气通知
     */
    private fun showRealtimeWeatherNotification(weather: Weather):Notification{
        val skyConToday = getSky(weather.daily.skyconSum[0].value)

        var iconDayOrNight = R.drawable.baseline_wb_sunny_black_24dp

        if(isDarkTheme(this)){
            iconDayOrNight = R.drawable.baseline_nights_stay_white_24dp
        }

        val notification =  NotificationCompat.Builder(this,"sun_rain_realtime")
            .setContentTitle("${weather.realtime.temperature.toInt()}°${getSky(weather.realtime.skycon).info}")
            .setSmallIcon(iconDayOrNight)
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

        activityWeatherBinding.itemProgressSunInclude.let {
            it.sunRiseTimeProgress.text = "↑$sunRiseTime"
            it.sunSetTimeProgress.text = "$sunSetTime↓"
            it.sunTimeProgressBar.progress = getSunProgressFromTimes(sunRiseTime,sunSetTime,serverTimeText)
        }
    }

    private fun judgeTimeInDay():Int{
        val date = Date()
        val df = SimpleDateFormat("HH", Locale.getDefault())

        return when (Integer.parseInt(df.format(date))) {
            in 0..5 -> {
                0
            }
            in 6..11 -> {
                1
            }
            12 -> {
                3
            }
            in 13..18 -> {
                4
            }
            else -> {
                5
            }
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

    private fun initDayForecastItems(daily: DailyResponse.Daily):ArrayList<DayForecastItem>{
        val dayForecastItemList = ArrayList<DayForecastItem>(daily.temperature.size)

        //使用此处数字约束首页出现的天气预报个数
        var maxNum = SunRainApplication.settingsPreference.getString("forecastItemsNum_List","5")
            ?.toInt()
        if (maxNum == null){
            maxNum = 5
        }
        maxNum -= 1
        for(i in 0..maxNum){
            val skycon = daily.skyconSum[i]
            val temp = daily.temperature[i]

            dayForecastItemList.add(DayForecastItem(skycon,temp))
        }
        return dayForecastItemList
    }

    class TemperatureChartXAxisFormatter : ValueFormatter() {
        private val days = arrayOf("今天", "明天", "后天", "大后天", "四天后")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt()) ?: value.toString()
        }
    }
}
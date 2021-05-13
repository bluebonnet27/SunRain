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
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.rainy.weahter_bg_plug.utils.WeatherUtil
import com.ti.sunrain.R
import com.ti.sunrain.SunRainApplication
import com.ti.sunrain.logic.ActivitySet
import com.ti.sunrain.logic.model.*
import com.ti.sunrain.logic.model.dayforecast.DayForecastItem
import com.ti.sunrain.ui.about.AboutActivity
import com.ti.sunrain.ui.air.AirActivity
import com.ti.sunrain.ui.covid.CovidSpecial
import com.ti.sunrain.ui.daily.DailyinforActivity
import com.ti.sunrain.ui.minutely.MinutelyActivity
import com.ti.sunrain.ui.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.air.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.forecast_chart.*
import kotlinx.android.synthetic.main.half_item_air.*
import kotlinx.android.synthetic.main.half_item_minutely_rain.*
import kotlinx.android.synthetic.main.hourly.*
import kotlinx.android.synthetic.main.item_progress_sun.*
import kotlinx.android.synthetic.main.life_index.*
import kotlinx.android.synthetic.main.minutely_rain.*
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

        //welcome
        val welcomeData = SunRainApplication.settingsPreference.getBoolean("welcome",true)
        if(welcomeData){
            AlertDialog.Builder(this)
                .setTitle("隐私政策")
                .setMessage("1. 当您使用晴雨时需要提供网络权限，用于将您输入的城市信息使用彩云天气的" +
                        "第三方接口搜索天气。晴雨会将上一次搜索的记录保存在本地。通过清除软件数据，可" +
                        "以将已保存的数据清除。晴雨不需要其他任何权限。\n"+
                        "2. 如果您对您的隐私有任何疑问或者需要解释的，请通过产品中的反馈方式与开发者" +
                        "取得联系。 如您不同意本协议或其中的任何条款的，您应停止使用晴雨。")
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

        if(SunRainApplication.settingsPreference.getBoolean("title_refresh_switch",false)){
            weatherToolBar.setOnClickListener {
                refreshWeather()
                Snackbar.make(swipeRefresh,"已发送刷新",Snackbar.LENGTH_SHORT).show()
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
        val minutelyReturn = weather.minutely
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

        //这行代码是测试天气动态背景的
        //nowAnimation.changeWeather(WeatherUtil.WeatherType.thunder)
        nowAnimation.changeWeather(getSkyAni(realtime.skycon))
        //middleRainy,heavyRainy,foggy,all snow,all rest

        //now.xml 动态背景
        val festivalBackgroundValue = SunRainApplication.settingsPreference
            .getBoolean("festival_bg_switch",true)
        val originBackground = (getSky(realtime.skycon).bg)
        if(festivalBackgroundValue){
            drawerLayout.setBackgroundResource(changeBackgroundByHours(originBackground
                ,judgeTimeInDay()))
        }
        else{
            drawerLayout.setBackgroundResource(originBackground)
        }

        //progressbar 数据
        initProgressBar(daily,serverTimeText)

        //forecast.xml 数据
        forecastDesc.text = "此刻天气" + weather.realtime.lifeIndex.comfort.desc +
                " 紫外线" + weather.realtime.lifeIndex.ultraviolet.desc
//        forecastLayout.removeAllViews()
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
        forecastRecyclerLayout.layoutManager = dayForcastLayoutManager

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
        forecastRecyclerLayout.adapter = dayForecastAdapter

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

        //temperatureChart
        //可见性
        val isForecastChartVisible = SunRainApplication.settingsPreference
            .getBoolean("forecast_chart_switch",false)
        if(isForecastChartVisible){
            tempChartCard.visibility = VISIBLE
        }else{
            tempChartCard.visibility = GONE
        }

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
        temperatureChart.data = dataTemp

        //折线图背景
        temperatureChart.xAxis.setDrawGridLines(false)
        temperatureChart.axisLeft.setDrawGridLines(false)

        //右下角英文字母
        temperatureChart.description.isEnabled = false

        //图例
        temperatureChart.legend.isEnabled = false

        //x轴
        val xAxis = temperatureChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1F // 加上这个就不会出现坐标点不对应的情况，但我不知道这是干啥的
        xAxis.valueFormatter = TemperatureChartXAxisFormatter()

        //y轴
        temperatureChart.axisRight.isEnabled = false
        temperatureChart.axisLeft.isEnabled = false

        //不允许触摸
        temperatureChart.setTouchEnabled(false)

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
                .setTitle(lifeIndex.coldRisk[0].desc)
                .setMessage("明天的： ${lifeIndex.coldRisk[1].desc}\n"+
                            "后天的： ${lifeIndex.coldRisk[2].desc}\n"+
                            "大后天： ${lifeIndex.coldRisk[3].desc}")
                .show()
        }

        dressingItem.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(lifeIndex.dressing[0].desc)
                .setMessage("明天的： ${lifeIndex.dressing[1].desc}\n"+
                            "后天的： ${lifeIndex.dressing[2].desc}\n"+
                            "大后天： ${lifeIndex.dressing[3].desc}")
                .show()
        }

        ultravioletItem.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(lifeIndex.ultraviolet[0].desc)
                .setMessage("明天的： ${lifeIndex.ultraviolet[1].desc}\n"+
                            "后天的： ${lifeIndex.ultraviolet[2].desc}\n"+
                            "大后天： ${lifeIndex.ultraviolet[3].desc}")
                .show()
        }

        carWashingItem.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(lifeIndex.carWashing[0].desc)
                .setMessage("明天的： ${lifeIndex.carWashing[1].desc}\n"+
                            "后天的： ${lifeIndex.carWashing[2].desc}\n"+
                            "大后天： ${lifeIndex.carWashing[3].desc}")
                .show()
        }

        comfortItem.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(lifeIndex.comfort[0].desc)
                .setMessage("明天的： ${lifeIndex.comfort[1].desc}\n"+
                            "后天的： ${lifeIndex.comfort[2].desc}\n"+
                            "大后天： ${lifeIndex.comfort[3].desc}")
                .show()
        }

        windDailyItem.setOnClickListener { view->
            Snackbar.make(view,"请前往对应日期页查看",Snackbar.LENGTH_SHORT).show()
        }

        weatherLayout.visibility = VISIBLE

        //air.xml
        initAirPie(weather)

        //空气页面
        airCard.setOnClickListener {
            val airActivityIntent = Intent(this,AirActivity::class.java)
            airActivityIntent.putExtra("weather",Gson().toJson(weather))
            startActivity(airActivityIntent)
        }

        //分钟降水页面
        minutelyCard.setOnClickListener {
            val minutelyActivityIntent = Intent(this,MinutelyActivity::class.java)
            minutelyActivityIntent.putExtra("weather",Gson().toJson(weather))
            startActivity(minutelyActivityIntent)
        }

        //hourly 数据
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        hourlyLayout.layoutManager = layoutManager
        val adapter = HourlyAdapter(initHourlyItemList(hourlyReturn))
        hourlyLayout.adapter = adapter

        hourlyDescText.text = hourlyReturn.description

        //分钟降水
        val minuteLayoutManager = LinearLayoutManager(this)
        minuteLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        minutelyLayout.layoutManager = minuteLayoutManager
        val minuteAdapter = MinutelyAdapter(initMinutelyItemList(minutelyReturn))
        minutelyLayout.adapter = minuteAdapter

        minutelyDescText.text = minutelyReturn.description

        //首页分钟降水
        minutelyCardDescIconHS.setImageResource(transferData1MinuteToIcon(getRainData1Minute(minutelyReturn)))
        minutelyCardIcon.setImageResource(minuteAdapter.getRainIconDesc(getRainData1Minute(minutelyReturn)))
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
        val isPositive = precipitation > 0

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
     * 填充空气指数的数据，绘图
     */
    private fun initAirPie(weather: Weather){
        //air.xml 数列写入数据
        airDesc.text = weather.realtime.airQuality.description.chn

        airDescTextHalf.text = weather.realtime.airQuality.description.chn

        val aq = weather.realtime.airQuality

        airDescHalfAQI.text = aq.aqi.chn.toInt().toString()
        airDescHalfAQIProgress.progress = aq.aqi.chn.toInt()

        pm25Num.text = "${aq.pm25} μg/m3"
        pm10Num.text = "${aq.pm10} μg/m3"
        no2Num.text = "${aq.no2} μg/m3"
        o3Num.text = "${aq.o3} μg/m3"
        so2Num.text = "${aq.so2} μg/m3"
        coNum.text = "${aq.co} mg/m3"

        pm25Item.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("PM 2.5")
                .setMessage("细颗粒物又称细粒、细颗粒、PM2.5。细颗粒物指环境空气中空气动力学当量直径小于" +
                        "等于 2.5 微米的颗粒物。它能较长时间悬浮于空气中，其在空气中含量浓度越高，就代表空" +
                        "气污染越严重。虽然PM2.5只是地球大气成分中含量很少的组分，但它对空气质量和能见度等" +
                        "有重要的影响。与较粗的大气颗粒物相比，PM2.5粒径小，面积大，活性强，易附带有毒、有" +
                        "害物质（例如，重金属、微生物等），且在大气中的停留时间长、输送距离远，因而对人体健" +
                        "康和大气环境质量的影响更大。")
                .setPositiveButton(getString(R.string.ok), null)
                .show()
        }

        pm10Item.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("PM 10")
                .setMessage("可吸入颗粒物，通常是指粒径在10微米以下的颗粒物，又称PM10。可吸入颗粒物在" +
                        "环境空气中持续的时间很长，对人体健康和大气能见度的影响都很大。通常来自在未铺的" +
                        "沥青、水泥的路面上行驶的机动车、材料的破碎碾磨处理过程以及被风扬起的尘土。可吸入" +
                        "颗粒物被人吸入后，会积累在呼吸系统中，引发许多疾病，对人类危害大。")
                .setPositiveButton(getString(R.string.ok), null)
                .show()
        }

        o3Item.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("臭氧")
                .setMessage("当人类生活区周边的臭氧浓度超过一定限值，就将造成灰疆和光化学烟雾等污染，严重" +
                        "影响正常生产与生活。")
                .setPositiveButton(getString(R.string.ok), null)
                .show()
        }

        no2Item.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("二氧化氮")
                .setMessage("二氧化氮在臭氧的形成过程中起着重要作用。人为产生的二氧化氮主要来自高温燃烧过" +
                        "程的释放，比如机动车尾气、锅炉废气的排放等。 二氧化氮还是酸雨的成因之一，所带来的" +
                        "环境效应多种多样，包括：对湿地和陆生植物物种之间竞争与组成变化的影响，大气能见度" +
                        "的降低，地表水的酸化、富营养化（由于水中富含氮、磷等营养物藻类大量繁殖而导致缺" +
                        "氧）以及增加水体中有害于鱼类和其它水生生物的毒素含量。")
                .setPositiveButton(getString(R.string.ok), null)
                .show()
        }

        so2Item.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("二氧化硫")
                .setMessage("二氧化硫是最常见、最简单、有刺激性的硫氧化物，化学式SO2，无色气体，大气主要" +
                        "污染物之一。2017年10月27日，世界卫生组织国际癌症研究机构公布的致癌物清单初步整" +
                        "理参考，二氧化硫在3类致癌物清单中。")
                .setPositiveButton(getString(R.string.ok), null)
                .show()
        }

        coItem.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("一氧化碳")
                .setMessage("一氧化碳（carbon monoxide），一种碳氧化合物，化学式为CO，分子量为28.0101" +
                        "，通常状况下为是无色、无臭、无味的气体。具有毒性，较高浓度时能使人出现不同程度中" +
                        "毒症状，危害人体的脑、心、肝、肾、肺及其他组织，甚至电击样死亡，")
                .setPositiveButton(getString(R.string.ok), null)
                .show()
        }

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
        airPie.setCenterTextSize(20f)

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

    private fun initDayForecastItems(daily: DailyResponse.Daily):ArrayList<DayForecastItem>{
        val dayForecastItemList = ArrayList<DayForecastItem>(daily.temperature.size)
        for(i in daily.skyconSum.indices){
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
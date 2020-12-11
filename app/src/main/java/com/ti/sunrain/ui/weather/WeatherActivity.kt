package com.ti.sunrain.ui.weather

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.snackbar.Snackbar
import com.ti.sunrain.R
import com.ti.sunrain.SunRainApplication
import com.ti.sunrain.logic.model.*
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.air.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.forecast_chart.*
import kotlinx.android.synthetic.main.life_index.*
import kotlinx.android.synthetic.main.now.*
import kotlinx.android.synthetic.main.wind.*
import kotlinx.android.synthetic.main.wind.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProviders.of(this).get(WeatherViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

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
        swipeRefresh.setColorSchemeResources(R.color.light_blue_darken_4)
        refreshWeather()
        swipeRefresh.setOnRefreshListener {
            Snackbar.make(swipeRefresh,"已发送刷新",Snackbar.LENGTH_SHORT).show()
            refreshWeather()
        }

        tempChartCard.setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage("这个功能还在开发中")
                .setPositiveButton(this.getString(R.string.know),null)
                .show()
        }
    }

    /**
     * 刷新天气，利用经纬度
     */
    fun refreshWeather(){
        viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)
        swipeRefresh.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather){

        //获取ViewModel中的数据,以及toolbar数据填充
        weatherToolBar.title = viewModel.placeName

        val realtime = weather.realtime
        val daily = weather.daily
        val responseForRealtime = weather.realtimeResponse
        val windReturn = weather.wind

        val serverTime = responseForRealtime.getServerTime()
        val serverTimeText = viewModel.changeUNIXIntoString(serverTime)
        weatherToolBar.subtitle = "刷新于：${serverTimeText}"

        //now.xml数据注入
        val currentTempText = "${realtime.temperature.toInt()}"
        currentTemperature.text = currentTempText
        currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text
        val currentAQIDescInfor = realtime.airQuality.description.chn
        currentAQIDesc.text = currentAQIDescInfor
        //nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        //forecast.xml数据注入
        forecastLayout.removeAllViews()
        val days = daily.skyconDaylight.size

        val listHigh = ArrayList<Entry>()
        val listLow = ArrayList<Entry>()


        for(i in 0 until days){
            val skycon = daily.skyconDaylight[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                forecastLayout,false)

            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById<TextView>(R.id.temperatureInfo)

            val simpleDateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)

            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.weather_icon)
            skyInfo.text = sky.info

            listHigh.add(Entry(i.toFloat(),temperature.max))
            listLow.add(Entry(i.toFloat(),temperature.min))

            val tempText = "${temperature.min.toInt()}~${temperature.max.toInt()}℃"
            temperatureInfo.text = tempText

            forecastLayout.addView(view)
        }

        //temperatureChart
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

        class MyXAxisFormatter : ValueFormatter() {
            private val daysMe = arrayOf("Today","Tomorrow","3","4","5")
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return daysMe.getOrNull(value.toInt()) ?: value.toString()
            }
        }
        
        xAxis.valueFormatter = MyXAxisFormatter()

        temperatureChart.data = dataTemp


        //wind.xml
        windIconInfo.setImageResource(getWindIcon(getWindSpeed(windReturn.speed)))
        windDirectionInfo.text = "${getWindDirection(windReturn.direction)}风"
        windSpeedInfo.text = "风力${getWindSpeed(windReturn.speed).toString()}级"

        //lifeindex.xml 数据注入
        val lifeIndex = daily.lifeIndex
        coldRiskText.text = lifeIndex.coldRisk[0].desc //今天数据就是一组数据里的第一个
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        weatherLayout.visibility = View.VISIBLE

        //air.xml 数据注入
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
        dirtyDataSet.setColors(Color.parseColor(viewModel.getAQIColor(aq.aqi.chn.toInt())),
                                Color.parseColor("#eeeeee"))
        dirtyDataSet.valueTextSize = 0f

        airPie.holeRadius = 90f
        airPie.description.isEnabled = false

        airPie.transparentCircleRadius = 0f
        airPie.centerText = "AQI:${aq.aqi.chn}"
        airPie.setCenterTextSize(20f)

        if(isDarkTheme(SunRainApplication.context)){
            airPie.setCenterTextColor(Color.WHITE)
            airPie.setHoleColor(Color.BLACK)
        }else{
            airPie.setCenterTextColor(Color.BLACK)
            airPie.setHoleColor(Color.WHITE)
        }

        airPie.animateXY(4000,4000)

        airPie.legend.isEnabled = false

        val dirtyDataUse = PieData(dirtyDataSet)
        airPie.data = dirtyDataUse
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)

            R.id.settingsIcon -> Snackbar.make(swipeRefresh,"待开发",Snackbar.LENGTH_SHORT).show()
            R.id.aboutIcon -> Snackbar.make(swipeRefresh,"待开发",Snackbar.LENGTH_SHORT).show()
            R.id.shareIcon -> Snackbar.make(swipeRefresh,"待开发",Snackbar.LENGTH_SHORT).show()
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu,menu)
        return true
    }

    /**
     * Judge is dark theme or not
     */
    fun isDarkTheme(context:Context):Boolean{
        val flag = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return flag == Configuration.UI_MODE_NIGHT_YES
    }
}
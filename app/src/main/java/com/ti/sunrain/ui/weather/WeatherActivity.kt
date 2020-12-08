package com.ti.sunrain.ui.weather

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.snackbar.Snackbar
import com.ti.sunrain.R
import com.ti.sunrain.logic.model.Weather
import com.ti.sunrain.logic.model.getSky
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.air.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.life_index.*
import kotlinx.android.synthetic.main.now.*
import java.text.SimpleDateFormat
import java.util.*

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
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        //forecast.xml数据注入
        forecastLayout.removeAllViews()
        val days = daily.skyconDaylight.size
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

            val tempText = "${temperature.min.toInt()}~${temperature.max.toInt()}℃"
            temperatureInfo.text = tempText

            forecastLayout.addView(view)
        }

        //lifeindex.xml 数据注入
        val lifeIndex = daily.lifeIndex
        coldRiskText.text = lifeIndex.coldRisk[0].desc //今天数据就是一组数据里的第一个
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        weatherLayout.visibility = View.VISIBLE

        //air.xml 数据注入
        val aq = realtime.airQuality
        pm25Num.text = "${aq.pm25}"
        pm10Num.text = "${aq.pm10}"
        no2Num.text = "${aq.no2}"
        o3Num.text = "${aq.o3}"
        so2Num.text = "${aq.so2}"
        coNum.text = "${aq.co}"

        //air.xml 图绘制
        val dirtyData = ArrayList<PieEntry>()
        dirtyData.add(PieEntry(aq.pm25,"PM2.5"))
        dirtyData.add(PieEntry(aq.pm10,"PM10"))
        dirtyData.add(PieEntry(aq.no2,"NO2"))
        dirtyData.add(PieEntry(aq.o3,"O3"))
        dirtyData.add(PieEntry(aq.so2,"SO2"))
        dirtyData.add(PieEntry(aq.co,"CO"))

        val dirtyDataSet = PieDataSet(dirtyData,"")
        dirtyDataSet.setColors(Color.RED,Color.GREEN,Color.BLUE,Color.YELLOW,Color.GRAY,Color.BLACK)
        airPie.holeRadius = 0f
        airPie.description.isEnabled = false
        airPie.transparentCircleRadius = 0f

        //设置描述的字体大小（图中的  男性  女性）
        airPie.setEntryLabelTextSize(5f);
        //设置数据的字体大小  （图中的  44     56）
        dirtyDataSet.valueTextSize = 5f;

        val dirtyDataUse = PieData(dirtyDataSet)
        airPie.data = dirtyDataUse
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu,menu)
        return true
    }
}
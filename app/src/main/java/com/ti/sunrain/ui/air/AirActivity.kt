package com.ti.sunrain.ui.air

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.gson.Gson
import com.ti.sunrain.R
import com.ti.sunrain.SunRainApplication
import com.ti.sunrain.logic.ActivitySet
import com.ti.sunrain.logic.model.Weather
import kotlinx.android.synthetic.main.activity_air.*
import kotlinx.android.synthetic.main.activity_dailyinfor.*
import kotlinx.android.synthetic.main.item_air_piechart.*

class AirActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_air)

        //set
        ActivitySet.addActivity(this)

        //darkmode
        when(SunRainApplication.settingsPreference.getString("others_darkmode_list","0")){
            "0" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            "1" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            "2" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        }

        setSupportActionBar(airActivityToolBar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24dp)
        }

        val weatherJson = intent.getStringExtra("weather")
        val weather = Gson().fromJson(weatherJson, Weather::class.java)

        initToolBarAndFAB(weather)
        initPieChartAndDirtyProgressBar(weather)
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
        //标题
        supportActionBar?.title = weather.realtime.airQuality.description.chn
        //副标题
        airActivityToolBar.subtitle = "AQI:" + weather.realtime.airQuality.aqi.chn.toInt()
    }

    private fun initPieChartAndDirtyProgressBar(weather: Weather){
        //数据初始化
        val aq = weather.realtime.airQuality

        //co的单位是毫克，其他为微克，使用时记得将co数值乘一千
        val pm25Num = aq.pm25
        val pm10Num = aq.pm10
        val no2Num = aq.no2
        val o3Num = aq.o3
        val so2Num = aq.so2
        val coNum = aq.co

        //pieChart数据组
        val dirtyData = ArrayList<PieEntry>()

        dirtyData.add(PieEntry(pm25Num,"PM 2.5"))
        dirtyData.add(PieEntry(pm10Num,"PM 10"))
        dirtyData.add(PieEntry(no2Num,"NO2"))
        dirtyData.add(PieEntry(o3Num,"O3"))
        dirtyData.add(PieEntry(so2Num,"SO2"))
        dirtyData.add(PieEntry(coNum*1000,"CO"))

        val dirtyDataSet = PieDataSet(dirtyData,"")

        //设置各个数据的颜色
        dirtyDataSet.setColors(Color.parseColor("#d32f2f"),
            Color.parseColor("#c2185b"),
            Color.parseColor("#7b1fa2"),
            Color.parseColor("#303f9f"),
            Color.parseColor("#0097a7"),
            Color.parseColor("#e64a19"))
        //设置描述的位置
        dirtyDataSet.xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
        //设置数据的位置
        dirtyDataSet.yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
        //设置数据的字体大小  （图中的  44     56）
        dirtyDataSet.valueTextSize = 10f
        //设置描述连接线长度
        //dirtyDataSet.valueLinePart1Length = 0f

        itemAirPieChartChart.apply {
            //实体扇形的空心圆的半径   设置成0时就是一个圆 而不是一个环
            holeRadius = 30f
            //设置中心圆的颜色
            setHoleColor(Color.parseColor(getAQIColor(aq.aqi.chn.toInt())))
            //设置中心部分的字  （一般中间白色圆不隐藏的情况下才设置）
            centerText = weather.realtime.airQuality.aqi.chn.toInt().toString()
            //设置中心字的字体大小
            setCenterTextSize(16f)
            //中间半透明白色圆的半径    设置成0时就是隐藏
            transparentCircleRadius = 35f
            //设置描述的字体大小
            setEntryLabelTextSize(15f)
            //XY两轴混合动画
            //animateXY(2000,2000)
            //是否显示右下角描述
            description.isEnabled = false
            //百分比
            setUsePercentValues(true)
        }

        //图例
        val legend = itemAirPieChartChart.legend
        legend.isEnabled = false

        //数据应用
        val dirtyDataUse = PieData(dirtyDataSet)
        itemAirPieChartChart.data = dirtyDataUse
    }

    fun getAQIColor(aqiValue:Int):String{
        when (aqiValue) {
            in 0..50 -> {
                return "#7cb342"
            }
            in 51..100 -> {
                return "#ffff8d"
            }
            in 101..150 -> {
                return "#f9a825"
            }
            in 151..200 -> {
                return "#e53935"
            }
            in 201..300 -> {
                return "#880e4f"
            }
            else -> {
                return "#212121"
            }
        }
    }
}
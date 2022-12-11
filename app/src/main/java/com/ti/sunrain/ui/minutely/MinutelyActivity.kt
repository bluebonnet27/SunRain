package com.ti.sunrain.ui.minutely

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.ti.sunrain.R
import com.ti.sunrain.SunRainApplication
import com.ti.sunrain.databinding.ActivityMinutelyBinding
import com.ti.sunrain.logic.ActivitySet
import com.ti.sunrain.logic.model.MinutelyItem
import com.ti.sunrain.logic.model.Weather
import com.ti.sunrain.ui.weather.MinutelyAdapter


class MinutelyActivity : AppCompatActivity() {
    private lateinit var activityMinutelyBinding: ActivityMinutelyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //使用viewBinding代替ktx插件
        activityMinutelyBinding = ActivityMinutelyBinding.inflate(layoutInflater)
        setContentView(activityMinutelyBinding.root)

        ActivitySet.addActivity(this)

        //darkmode
        when(SunRainApplication.settingsPreference.getString("others_darkmode_list","0")){
            "0" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            "1" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            "2" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        }

        setSupportActionBar(activityMinutelyBinding.minutelyActivityToolBar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24dp)
        }

        val weatherJson = intent.getStringExtra("weather")
        val weather = Gson().fromJson(weatherJson, Weather::class.java)

        initToolBarAndFAB(weather)
        initMinutelyRain(weather)
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

    private fun initToolBarAndFAB(weather: Weather){
        //Title
        supportActionBar?.title = weather.minutely.description
        //Sub Title
        activityMinutelyBinding.minutelyActivityToolBar.subtitle = "2 hours:" +
                (weather.minutely.precipitation_2h[0]*100).toString() + "%"
    }

    private fun initMinutelyRain(weather: Weather){
        //分钟降水
        val minuteLayoutManager = LinearLayoutManager(this)
        minuteLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        val minutelyLayout = findViewById<RecyclerView>(R.id.minutelyLayout)
        minutelyLayout.layoutManager = minuteLayoutManager
        val minuteAdapter = MinutelyAdapter(initMinutelyItemList(weather))
        minutelyLayout.adapter = minuteAdapter

        val minutelyDescText = findViewById<TextView>(R.id.minutelyDescText)
        minutelyDescText.text = weather.minutely.description
    }

    /**
     * 填充分钟降水数据
     */
    private fun initMinutelyItemList(weather: Weather)
            :ArrayList<MinutelyItem>{
        val minutely = weather.minutely
        val minutelyItems = ArrayList<MinutelyItem>(minutely.precipitation.size)
        //If you want to change the type of precipitation, please change the two params
        for(j in minutely.precipitation.indices){
            val minuteDescription = minutely.precipitation[j]

            minutelyItems.add(MinutelyItem(minuteDescription))
        }

        return minutelyItems
    }
}
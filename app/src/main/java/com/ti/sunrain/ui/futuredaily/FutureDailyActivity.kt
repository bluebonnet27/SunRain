package com.ti.sunrain.ui.futuredaily

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.ti.sunrain.R
import com.ti.sunrain.SunRainApplication
import com.ti.sunrain.databinding.ActivityFutureDailyBinding
import com.ti.sunrain.logic.ActivitySet
import com.ti.sunrain.logic.model.DailyResponse
import com.ti.sunrain.logic.model.futuredaily.FutureDailyItem

class FutureDailyActivity : AppCompatActivity() {
    lateinit var activityFutureDailyBinding: ActivityFutureDailyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityFutureDailyBinding = ActivityFutureDailyBinding.inflate(layoutInflater)
        setContentView(activityFutureDailyBinding.root)

        //加入set组，控制全局activity
        ActivitySet.addActivity(this)

        //暗黑模式控制
        when(SunRainApplication.settingsPreference.getString("others_darkmode_list","0")){
            "0" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            "1" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            "2" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        }

        //toolbar美化
        setSupportActionBar(activityFutureDailyBinding.futureDailyActivityToolBar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24dp)
        }

        //从intent取数据
        val dailyJson = intent.getStringExtra("daily")
        val daily = Gson().fromJson(dailyJson,DailyResponse.Daily::class.java)

        //主体
        initToolbar()
        initFutureDailyData(daily)
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

    private fun initToolbar(){
        supportActionBar?.title = "未来十五天天气预报"
        activityFutureDailyBinding.futureDailyActivityToolBar.subtitle = "以最新预报为准"
    }

    private fun initFutureDailyData(daily: DailyResponse.Daily){
        val futureDailyLineLayoutManager = LinearLayoutManager(this)
        futureDailyLineLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        activityFutureDailyBinding.futureDailyActivityRecyclerLayout.layoutManager = futureDailyLineLayoutManager

        val futureDailyAdapter = FutureDailyAdapter(initFutureDailyItems(daily))
        futureDailyAdapter.setOnItemClickListener(object :FutureDailyAdapter.OnItemClickListener{
            override fun onItemClick(view: View, position: Int, weekday: String) {
                //DailyInfor的设计上有重大缺陷（可以用daily传却传了weather），因此此处暂时不写点击方案，
                //等修正缺陷后再写
                Toast.makeText(this@FutureDailyActivity, "点击功能开发中", Toast.LENGTH_SHORT).show()
            }
        })
        activityFutureDailyBinding.futureDailyActivityRecyclerLayout.adapter = futureDailyAdapter
    }

    private fun initFutureDailyItems(daily: DailyResponse.Daily):ArrayList<FutureDailyItem>{
        val futureDailyItemList = ArrayList<FutureDailyItem>(daily.temperature.size)

        for(i in daily.skyconSum.indices){
            //日期+天气
            val skyconDay = daily.skyconDaylight[i]
            val skyconNight = daily.skyconNight[i]
            //
            val tempTwo = daily.temperature[i]
            //
            val wind = daily.wind[i].avgWind
            //
            val airQuality = daily.aqi.aqiList[i]

            futureDailyItemList.add(FutureDailyItem(skyconDay,skyconNight,tempTwo,wind,airQuality))

            Log.d("Item!","$skyconDay,$skyconNight,$tempTwo,$wind,$airQuality")
        }

        return futureDailyItemList
    }
}
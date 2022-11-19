package com.ti.sunrain.ui.covid

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.ti.sunrain.R
import com.ti.sunrain.SunRainApplication
import com.ti.sunrain.databinding.ActivityCovidSpecialBinding
import com.ti.sunrain.logic.ActivitySet

class CovidSpecialActivity : AppCompatActivity() {
    private lateinit var activityCovidSpecialBinding: ActivityCovidSpecialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //使用viewBinding代替ktx插件
        activityCovidSpecialBinding = ActivityCovidSpecialBinding.inflate(layoutInflater)
        setContentView(activityCovidSpecialBinding.root)

        //set
        ActivitySet.addActivity(this)

        //darkmode
        when(SunRainApplication.settingsPreference.getString("others_darkmode_list","0")){
            "0" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            "1" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            "2" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        }

        //2022年8月10日 解决 main page 无法加载网页问题
        val mainPageSettings = activityCovidSpecialBinding.mainPage.settings
        mainPageSettings.javaScriptEnabled = true

        activityCovidSpecialBinding.mainPage.loadUrl("https://www.tianditu.gov.cn/coronavirusmap/")

        setSupportActionBar(activityCovidSpecialBinding.covidToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        //set
        ActivitySet.removeActivity(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.covidtoolbar_menu,menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if(isDarkTheme(this)){
            menu?.getItem(0)?.icon =
                ContextCompat.getDrawable(this,R.drawable.baseline_refresh_white_24dp)
            menu?.getItem(1)?.icon =
                ContextCompat.getDrawable(this,R.drawable.baseline_computer_white_24dp)
            menu?.getItem(2)?.icon =
                ContextCompat.getDrawable(this,R.drawable.baseline_delete_white_24dp)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()

            R.id.refreshCovidPage -> activityCovidSpecialBinding.mainPage.reload()
            R.id.openInBrowser -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://www.tianditu.gov.cn/coronavirusmap/")
                startActivity(intent)
            }
            R.id.clearHistoryMe -> activityCovidSpecialBinding.mainPage.clearHistory()
        }
        return true
    }

    private fun isDarkTheme(context: Context):Boolean{
        val flag = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return flag == Configuration.UI_MODE_NIGHT_YES
    }
}
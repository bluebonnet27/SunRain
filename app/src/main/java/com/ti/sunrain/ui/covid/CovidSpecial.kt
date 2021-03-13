package com.ti.sunrain.ui.covid

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import com.ti.sunrain.R
import com.ti.sunrain.SunRainApplication
import com.ti.sunrain.logic.ActivitySet
import kotlinx.android.synthetic.main.activity_covid_special.*

class CovidSpecial : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_covid_special)

        //set
        ActivitySet.addActivity(this)

        //darkmode
        when(SunRainApplication.settingsPreference.getString("others_darkmode_list","0")){
            "0" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            "1" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            "2" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        }

        mainPage.loadUrl("https://voice.baidu.com/act/newpneumonia/newpneumonia")

        setSupportActionBar(covidToolbar)
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

            R.id.refreshCovidPage -> mainPage.reload()
            R.id.openInBrowser -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://voice.baidu.com/act/newpneumonia/newpneumonia")
                startActivity(intent)
            }
            R.id.clearHistoryMe -> mainPage.clearHistory()
        }
        return true
    }
}
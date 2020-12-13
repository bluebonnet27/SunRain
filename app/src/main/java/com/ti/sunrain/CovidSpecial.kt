package com.ti.sunrain

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_covid_special.*

class CovidSpecial : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_covid_special)

        mainPage.loadUrl("https://voice.baidu.com/act/newpneumonia/newpneumonia")

        setSupportActionBar(covidToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.covidtoolbar_menu,menu)
        return true
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
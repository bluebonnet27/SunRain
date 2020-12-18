package com.ti.sunrain.ui.daily

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.ti.sunrain.R
import kotlinx.android.synthetic.main.activity_dailyinfor.*

class DailyinforActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dailyinfor)

        setSupportActionBar(dailyInforToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24dp)
        }

        val dateInfor = intent.getStringExtra("date_information")
        supportActionBar?.title = dateInfor
        val temparatureInfor = intent.getStringExtra("temp_information")
        dailyInforToolbar.subtitle = temparatureInfor
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }
        return true
    }
}
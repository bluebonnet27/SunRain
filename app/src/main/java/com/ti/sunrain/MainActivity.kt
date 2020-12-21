package com.ti.sunrain

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ti.sunrain.logic.ActivitySet

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //set
        ActivitySet.addActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        //set
        ActivitySet.removeActivity(this)
    }
}
package com.ti.sunrain.logic.model

import android.widget.Toast
import com.ti.sunrain.R
import com.ti.sunrain.SunRainApplication

/**
 * @author: tihon
 * @date: 2020/12/9
 * @description:
 */

fun getWindDirection(originDirection:Float):String{
    if (originDirection in 348.75..360.0) {
        return "北"
    } else if (originDirection in 0.0..11.25) {
        return "北"
    } else if (11.25 < originDirection && originDirection <= 33.75) {
        return "东北偏北"
    } else if (33.75 < originDirection && originDirection <= 56.25) {
        return "东北"
    } else if (56.25 < originDirection && originDirection <= 78.75) {
        return "东北偏东"
    } else if (78.75 < originDirection && originDirection <= 101.25) {
        return "东"
    } else if (101.25 < originDirection && originDirection <= 123.75) {
        return "东南偏东"
    } else if (123.75 < originDirection && originDirection <= 146.25) {
        return "东南"
    } else if (146.25 < originDirection && originDirection <= 168.75) {
        return "东南偏南"
    } else if (168.75 < originDirection && originDirection <= 191.25) {
        return "南"
    } else if (191.25 < originDirection && originDirection <= 213.75) {
        return "西南偏南"
    } else if (213.75 < originDirection && originDirection <= 236.25) {
        return "西南"
    } else if (236.25 < originDirection && originDirection <= 258.75) {
        return "西南偏西"
    } else if (258.75 < originDirection && originDirection <= 281.25) {
        return "西"
    } else if (281.25 < originDirection && originDirection <= 303.75) {
        return "西北偏西"
    } else if (303.75 < originDirection && originDirection <= 326.25) {
        return "西北"
    } else if (326.25 < originDirection && originDirection < 348.75) {
        return "西北偏北"
    } else {
        Toast.makeText(SunRainApplication.context, "风向错误，请联系开发者", Toast.LENGTH_SHORT).show()
        return "未知风向"
    }
}

fun getWindSpeed(originSpeed: Float):Int{
    val originSpeedX = (originSpeed/3.6).toFloat()
    return if (0 <= originSpeedX && originSpeedX < 0.3) 0
    else if (0.3 <= originSpeedX && originSpeedX < 1.6) 1
    else if (1.6 <= originSpeedX && originSpeedX < 3.3) 2
    else if (3.3 <= originSpeedX && originSpeedX < 5.4) 3
    else if (5.4 <= originSpeedX && originSpeedX < 7.9) 4
    else if (7.9 <= originSpeedX && originSpeedX < 10.7) 5
    else if (10.7 <= originSpeedX && originSpeedX < 13.8) 6
    else if (13.8 <= originSpeedX && originSpeedX < 17.1) 7
    else if (17.1 <= originSpeedX && originSpeedX < 20.7) 8
    else if (20.7 <= originSpeedX && originSpeedX < 24.4) 9
    else if (24.5 <= originSpeedX && originSpeedX < 28.4) 10
    else if (28.4 <= originSpeedX && originSpeedX < 32.6) 11
    else 12
}

fun getWindIcon(speed:Int)=when(speed){
    0 -> R.drawable.ic_wind0
    1 -> R.drawable.ic_wind1
    2 -> R.drawable.ic_wind2
    3 -> R.drawable.ic_wind3
    4 -> R.drawable.ic_wind4
    5 -> R.drawable.ic_wind5
    6 -> R.drawable.ic_wind6
    7 -> R.drawable.ic_wind7
    8 -> R.drawable.ic_wind8
    9 -> R.drawable.ic_wind9
    10 -> R.drawable.ic_wind10
    11 -> R.drawable.ic_wind11
    12 -> R.drawable.ic_wind12
    else -> R.drawable.ic_wind
}
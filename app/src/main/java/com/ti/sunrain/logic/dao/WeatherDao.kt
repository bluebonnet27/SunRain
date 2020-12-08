package com.ti.sunrain.logic.dao

import java.text.SimpleDateFormat
import java.util.*

/**
 * @author: tihon
 * @date: 2020/12/8
 * @description:
 */
object WeatherDao {

    /**
     * Realtime UNIX changed into Time
     */
    fun changeUNIXIntoString(unixTime:Long):String{
        val utcTime : SimpleDateFormat = SimpleDateFormat("yyyy年MM月dd日HH时mm分",Locale.getDefault())
        utcTime.timeZone = TimeZone.getTimeZone("GMT+8:00")
        return utcTime.format(unixTime*1000)
    }
}
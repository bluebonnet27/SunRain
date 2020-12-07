package com.ti.sunrain.logic.model

import com.ti.sunrain.R

/**
 * @author: tihon
 * @date: 2020/12/7
 * @description:
 */
class Sky(val info:String,
            val weather_icon:Int,
            val bg:Int)

private val sky = mapOf(
    "CLEAR_DAY" to Sky("晴", R.drawable.ic_clear_day,R.drawable.bg_clear_day),
    "CLEAR_NIGHT" to Sky("晴",R.drawable.ic_clear_night,R.drawable.bg_clear_night),

    "PARTLY_CLOUDY_DAY" to Sky("多云",R.drawable.ic_partly_cloud_day,R.drawable.bg_partly_cloudy_day ),
    "PARTLY_CLOUDY_NIGHT" to Sky("多云",R.drawable.ic_partly_cloud_night,R.drawable.bg_partly_cloudy_night),

    "CLOUDY" to Sky("阴",R.drawable.ic_cloudy,R.drawable.bg_cloudy),

    "LIGHT_HAZE" to Sky("轻度雾霾",R.drawable.ic_light_haze,R.drawable.bg_fog),
    "MODERATE_HAZE" to Sky("中度雾霾",R.drawable.ic_moderate_haze,R.drawable.bg_fog),
    "HEAVY_HAZE" to Sky("重度雾霾",R.drawable.ic_heavy_haze,R.drawable.bg_fog),

    "LIGHT_RAIN" to Sky("小雨",R.drawable.ic_light_rain,R.drawable.bg_rain),
    "MODERATE_RAIN" to Sky("中雨",R.drawable.ic_moderate_rain,R.drawable.bg_rain),
    "HEAVY_RAIN" to Sky("大雨",R.drawable.ic_heavy_rain,R.drawable.bg_rain),
    "STORM_RAIN" to Sky("暴雨",R.drawable.ic_storm_rain,R.drawable.bg_rain),

    "FOG" to Sky("雾",R.drawable.ic_light_haze,R.drawable.bg_fog),

    "LIGHT_SNOW" to Sky("小雪",R.drawable.ic_light_snow,R.drawable.bg_snow),
    "MODERATE_SNOW" to Sky("中雪",R.drawable.ic_moderate_snow,R.drawable.bg_snow),
    "HEAVY_SNOW" to Sky("大雪",R.drawable.ic_heavy_snow,R.drawable.bg_snow),
    "STORM_SNOW" to Sky("暴雪",R.drawable.ic_heavy_snow,R.drawable.bg_snow),

    "DUST" to Sky("浮尘",R.drawable.ic_light_haze,R.drawable.bg_fog),
    "WIND" to Sky("大风",R.drawable.ic_wind,R.drawable.bg_wind),

    //以下为书中提到，但彩云天气最新官网并没有提到的，由于map转换并不一定绝对匹配，为了程序的鲁棒性没有去掉
    "THUNDER_SHOWER" to Sky("雷阵雨",R.drawable.ic_thunder_shower,R.drawable.bg_rain),
    "SLEET" to Sky("雨夹雪",R.drawable.ic_sleet,R.drawable.bg_rain),
    "HAIL" to Sky("冰雹",R.drawable.ic_hail, R.drawable.bg_snow)
)

fun getSky(skycon:String):Sky{
    return sky[skycon]?: sky["CLEAR_DAY"]!!
    //判空运算，简化了，详细写法类似于 Java 的
}
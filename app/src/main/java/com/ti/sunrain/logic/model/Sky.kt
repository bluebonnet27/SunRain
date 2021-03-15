package com.ti.sunrain.logic.model

import com.rainy.weahter_bg_plug.utils.WeatherUtil
import com.ti.sunrain.R
import com.ti.sunrain.SunRainApplication

/**
 * @author: tihon
 * @date: 2020/12/7
 * @description:打算用基本函数加overwrite的方案，但我觉得这有点扯淡
 */
class Sky(val info:String,
            val weather_icon:Int,
            val bg:Int)

private val sky = mapOf(
    "CLEAR_DAY" to Sky(SunRainApplication.context.getString(R.string.clear_day), R.drawable.ic_clear_day,R.drawable.bg_sunnyday_new),
    "CLEAR_NIGHT" to Sky(SunRainApplication.context.getString(R.string.clear_day),R.drawable.ic_clear_night,R.drawable.bg_sunnynight_new),

    "PARTLY_CLOUDY_DAY" to Sky(SunRainApplication.context.getString(R.string.cloudy),R.drawable.ic_partly_cloud_day,R.drawable.bg_partly_cloudyday_new),
    "PARTLY_CLOUDY_NIGHT" to Sky(SunRainApplication.context.getString(R.string.cloudy),R.drawable.ic_partly_cloud_night,R.drawable.bg_partlycloudynight_new),

    "CLOUDY" to Sky(SunRainApplication.context.getString(R.string.overcast),R.drawable.ic_cloudy,R.drawable.bg_cloudy_new),

    "LIGHT_HAZE" to Sky(SunRainApplication.context.getString(R.string.mild_haze),R.drawable.ic_light_haze,R.drawable.bg_haze_light_new),
    "MODERATE_HAZE" to Sky(SunRainApplication.context.getString(R.string.moderate_haze),R.drawable.ic_moderate_haze,R.drawable.bg_haze_moderate_new),
    "HEAVY_HAZE" to Sky(SunRainApplication.context.getString(R.string.severe_haze),R.drawable.ic_heavy_haze,R.drawable.bg_haze_heavy_new),

    "LIGHT_RAIN" to Sky(SunRainApplication.context.getString(R.string.light_rain),R.drawable.ic_light_rain,R.drawable.bg_rain_light_new),
    "MODERATE_RAIN" to Sky(SunRainApplication.context.getString(R.string.moderate_rain),R.drawable.ic_moderate_rain,R.drawable.bg_rain_moderate_new),
    "HEAVY_RAIN" to Sky(SunRainApplication.context.getString(R.string.heavy_rain),R.drawable.ic_heavy_rain,R.drawable.bg_rain_heavy_new),
    "STORM_RAIN" to Sky(SunRainApplication.context.getString(R.string.storm_rain),R.drawable.ic_storm_rain,R.drawable.bg_rain_storm_new),

    "FOG" to Sky(SunRainApplication.context.getString(R.string.fog),R.drawable.ic_light_haze,R.drawable.bg_fog_new),

    "LIGHT_SNOW" to Sky(SunRainApplication.context.getString(R.string.light_snow),R.drawable.ic_light_snow,R.drawable.bg_snow_light_new),
    "MODERATE_SNOW" to Sky(SunRainApplication.context.getString(R.string.moderate_snow),R.drawable.ic_moderate_snow,R.drawable.bg_snow_moderate_new),
    "HEAVY_SNOW" to Sky(SunRainApplication.context.getString(R.string.heavy_snow),R.drawable.ic_heavy_snow,R.drawable.bg_snow_heavy),
    "STORM_SNOW" to Sky(SunRainApplication.context.getString(R.string.storm_snow),R.drawable.ic_heavy_snow,R.drawable.bg_snow_storm),

    "DUST" to Sky(SunRainApplication.context.getString(R.string.dust),R.drawable.ic_light_haze,R.drawable.bg_dust_new),
    "WIND" to Sky(SunRainApplication.context.getString(R.string.wind),R.drawable.ic_wind,R.drawable.bg_wind_new),

    //以下为书中提到，但彩云天气最新官网并没有提到的，由于map转换并不一定绝对匹配，为了程序的鲁棒性没有去掉
    "THUNDER_SHOWER" to Sky(SunRainApplication.context.getString(R.string.thunder_shower),R.drawable.ic_thunder_shower,R.drawable.bg_rain_light_new),
    "SLEET" to Sky(SunRainApplication.context.getString(R.string.sleet),R.drawable.ic_sleet,R.drawable.bg_rain_storm_new),
    "HAIL" to Sky(SunRainApplication.context.getString(R.string.hail),R.drawable.ic_hail, R.drawable.bg_snow_moderate_new)
)

private val skyAni = mapOf(
    "CLEAR_DAY" to WeatherUtil.WeatherType.sunny,
    "CLEAR_NIGHT" to WeatherUtil.WeatherType.sunnyNight,

    "PARTLY_CLOUDY_DAY" to WeatherUtil.WeatherType.cloudy,
    "PARTLY_CLOUDY_NIGHT" to WeatherUtil.WeatherType.cloudyNight,

    "CLOUDY" to WeatherUtil.WeatherType.overcast,

    "LIGHT_HAZE" to WeatherUtil.WeatherType.hazy,
    "MODERATE_HAZE" to WeatherUtil.WeatherType.hazy,
    "HEAVY_HAZE" to WeatherUtil.WeatherType.hazy,

    "LIGHT_RAIN" to WeatherUtil.WeatherType.lightRainy,
    "MODERATE_RAIN" to WeatherUtil.WeatherType.middleRainy,
    "HEAVY_RAIN" to WeatherUtil.WeatherType.heavyRainy,
    "STORM_RAIN" to WeatherUtil.WeatherType.heavyRainy,

    "FOG" to WeatherUtil.WeatherType.foggy,

    "LIGHT_SNOW" to WeatherUtil.WeatherType.lightSnow,
    "MODERATE_SNOW" to WeatherUtil.WeatherType.middleSnow,
    "HEAVY_SNOW" to WeatherUtil.WeatherType.heavySnow,
    "STORM_SNOW" to WeatherUtil.WeatherType.heavySnow,

    "DUST" to WeatherUtil.WeatherType.dusty,
    "WIND" to WeatherUtil.WeatherType.overcast,

    "THUNDER_SHOWER" to WeatherUtil.WeatherType.thunder,
    "SLEET" to WeatherUtil.WeatherType.middleSnow,
    "HAIL" to WeatherUtil.WeatherType.middleSnow
)

fun getSky(skycon:String):Sky{
    return sky[skycon]?: sky["CLEAR_DAY"]!!
    //判空运算，简化了，详细写法类似于 Java 的
}

fun getSkyAni(skycon: String):String{
    return skyAni[skycon]?: WeatherUtil.WeatherType.sunny
}
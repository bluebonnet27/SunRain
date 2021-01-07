package com.ti.sunrain.logic.model

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * @author: tihon
 * @date: 2020/12/6
 * @description:
 */
/**
 * 温度1
 * 天气1
 * 空气质量
 * 风向1
 * 日出日落1
 * 生活指数
 */
data class DailyResponse(val status:String,val result: Result){

    data class Result(val daily:Daily)

    data class Daily(val astro:List<Astro>,
                     @SerializedName("skycon")val skyconSum:List<Skycon>,
                     @SerializedName("skycon_08h_20h")val skyconDaylight:List<Skycon>,
                     @SerializedName("skycon_20h_32h")val skyconNight:List<Skycon>,
                     val temperature:List<Temperature>,
                     val wind:List<Wind>,
                     val humidity:List<Humidity>,
                     @SerializedName("air_quality")val aqi:AQI,
                     @SerializedName("life_index")val lifeIndex:LifeIndex,
                     val precipitation:List<Precipitation>)

    data class Astro(val date: Date,val sunrise:Sunrise,val sunset:Sunset)

    data class Sunrise(@SerializedName("time")val risetime:String)

    data class Sunset(@SerializedName("time")val settime: String)

    data class Skycon(val date: Date,val value:String)

    data class Temperature(val max:Float,val min:Float)

    data class Wind(val date: Date,
                    @SerializedName("max")val maxWind:WindDesc,
                    @SerializedName("min")val minWind:WindDesc,
                    @SerializedName("avg")val avgWind:WindDesc)

    data class Humidity(val date:String,
                        @SerializedName("max")val maxHumidity:Float,
                        @SerializedName("min")val minHumidity:Float,
                        @SerializedName("avg")val avgHumidity:Float)

    data class WindDesc(val speed:Float,val direction:Float )

    data class AQI(@SerializedName("aqi")val aqiList:List<AQIDesc>,
                   @SerializedName("pm25")val pm25List:List<PM25Desc>)

    data class AQIDesc(val date: String,val max:AQIMM,val min:AQIMM,val avg:AQIMM)

    data class PM25Desc(val date: String,val max:Float,val min:Float,val avg:Float)

    data class AQIMM(val chn:Float)

    data class LifeIndex(val coldRisk:List<LifeDescription>,
                         val carWashing:List<LifeDescription>,
                         val ultraviolet:List<LifeDescription>,
                         val dressing:List<LifeDescription>,
                         val comfort:List<LifeDescription>)

    data class LifeDescription(val index:String,val desc:String)

    data class Precipitation(val avg:Float)
}
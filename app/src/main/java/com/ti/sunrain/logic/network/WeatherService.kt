package com.ti.sunrain.logic.network

import com.ti.sunrain.SunRainApplication
import com.ti.sunrain.logic.model.DailyResponse
import com.ti.sunrain.logic.model.HourlyResponse
import com.ti.sunrain.logic.model.MinutelyResponse
import com.ti.sunrain.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author: tihon
 * @date: 2020/12/4
 * @description:查询天气的接口
 */
interface WeatherService {

    /**
     * 实时天气接口，返回RealtimeResponse，来自logic.model
     */
    @GET("v2.5/${SunRainApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng")lng:String,
                           @Path("lat")lat:String):Call<RealtimeResponse>
    /**
     * 未来天气接口，返回DailyResponse，来自logic.model
     */
    @GET("v2.5/${SunRainApplication.TOKEN}/{lng},{lat}/daily.json?dailysteps=15")
    fun getDailyWeather(@Path("lng")lng:String,
                        @Path("lat")lat:String):Call<DailyResponse>

    /**
     * 小时预报接口,返回HourlyResponse，来自logic.model
     */
    @GET("v2.5/${SunRainApplication.TOKEN}/{lng},{lat}/hourly.json")
    fun getHourlyWeather(@Path("lng")lng:String,
                         @Path("lat")lat:String):Call<HourlyResponse>

    /**
     * Raining interface
     */
    @GET("v2.5/${SunRainApplication.TOKEN}/{lng},{lat}/minutely.json")
    fun getMinutelyRain(@Path("lng")lng:String,
                        @Path("lat")lat:String):Call<MinutelyResponse>
}
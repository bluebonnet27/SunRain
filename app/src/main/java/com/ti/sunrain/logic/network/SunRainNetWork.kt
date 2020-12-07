package com.ti.sunrain.logic.network

import android.util.Log
import com.ti.sunnyweather.logic.network.WeatherService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


/**
 * @author: tihon
 * @date: 2020/12/3
 * @description:网络的单类
 */
object SunRainNetWork {

    private val placeService = ServiceCreator.create(PlaceService::class.java)

    /**
     * kotlin 协程返回搜索到的城市
     */
    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()

    private val weatherService = ServiceCreator.create(WeatherService::class.java)

    /**
     * 未来天气
     */
    suspend fun getDailyWeather(lng:String,lat:String) =
        weatherService.getDailyWeather(lng, lat).await()

    /**
     * 实时天气
     */
    suspend fun getRealtimeWeather(lng: String,lat: String) =
        weatherService.getRealtimeWeather(lng, lat).await()

    private suspend fun <T> Call<T>.await():T{
        return suspendCoroutine { continuation ->
            enqueue(object: Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if(body!=null){
                        continuation.resume(body)
                    }
                    else continuation.resumeWithException(
                        RuntimeException("Response body is null,空的body返回对象"))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}
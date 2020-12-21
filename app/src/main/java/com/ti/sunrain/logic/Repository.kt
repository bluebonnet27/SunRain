package com.ti.sunrain.logic

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.ti.sunrain.R
import com.ti.sunrain.logic.dao.PlaceDao
import com.ti.sunrain.logic.dao.WeatherDao
import com.ti.sunrain.logic.model.HourlyResponse
import com.ti.sunrain.logic.model.PlaceResponse.Place
import com.ti.sunrain.logic.model.Weather
import com.ti.sunrain.logic.network.SunRainNetWork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

/**
 * @author: tihon
 * @date: 2020/12/3
 * @description:
 */
object Repository {

    /**
     * 仓库层，分析返回数据并使用 livedata 持久化
     */
    fun searchPlaces(query: String) = fire(Dispatchers.IO){
            val placeResponse = SunRainNetWork.searchPlaces(query)
            if(placeResponse.status=="ok"){
                val places = placeResponse.places
                Result.success(places)
            }else{
                Result.failure(RuntimeException("Response status is ${placeResponse.status}"))
            }
    }

    /**
     * 刷新天气,利用lng和lat
     */
    fun refreshWeather(lng:String,lat:String) = fire(Dispatchers.IO){
        coroutineScope {

            val deferredRealtime = async {
                SunRainNetWork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                SunRainNetWork.getDailyWeather(lng, lat)
            }
            val deferredHourly = async {
                SunRainNetWork.getHourlyWeather(lng, lat)
            }

            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            val hourlyResponse = deferredHourly.await()

            if(realtimeResponse.status == "ok" &&
                dailyResponse.status == "ok" &&
                hourlyResponse.status == "ok"){
                val weather = Weather(realtimeResponse.result.realtime,
                                        dailyResponse.result.daily,
                                        realtimeResponse,
                                        realtimeResponse.result.realtime.wind,
                                        hourlyResponse.result.hourly)
                Result.success(weather)
            }else{
                Result.failure(
                    RuntimeException(
                        "WARNING: realtime status is ${realtimeResponse.status} "+
                                "WARNING: realtime status is ${dailyResponse.status} "
                    )
                )
            }
        }
    }


    private fun <T> fire(context:CoroutineContext,block:suspend()-> Result<T>): LiveData<Result<T>> {
        return liveData<Result<T>>(context) {
            val result = try {
                block()
            }catch (e:Exception){
                Result.failure<T>(e)
            }
            emit(result)
        }
    }

    /**
     * 持久化存储，存储访问的城市
     */
    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    /**
     * 读取城市信息
     */
    fun getSavedPlace() = PlaceDao.getSavedPlace()

    /**
     * 判断城市是否在列表里
     */
    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

    /**
     * From WeatherDao,change UNIX time into String
     */
    fun changeUNIXIntoString(unixTime:Long)=WeatherDao.changeUNIXIntoString(unixTime)
}
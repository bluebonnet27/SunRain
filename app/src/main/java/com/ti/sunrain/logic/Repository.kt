package com.ti.sunrain.logic

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.ti.sunrain.logic.dao.PlaceDao
import com.ti.sunrain.logic.dao.WeatherDao
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
 * @description:在本地没有数据（缓存）的情况下去网络层获取数据，但我觉得没必要就去掉了本地缓存
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
            val deferredMinutely = async {
                SunRainNetWork.getMinutelyRain(lng, lat)
            }

            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            val hourlyResponse = deferredHourly.await()
            val minutelyResponse = deferredMinutely.await()

            if(realtimeResponse.status == "ok" &&
                dailyResponse.status == "ok" &&
                hourlyResponse.status == "ok" &&
                minutelyResponse.status == "ok"){
                val weather = Weather(realtimeResponse.result.realtime,
                                        dailyResponse.result.daily,
                                        realtimeResponse,
                                        realtimeResponse.result.realtime.wind,
                                        hourlyResponse.result.hourly,
                                        minutelyResponse.result.minutely)
                Result.success(weather)
            }else{
                Result.failure(
                    RuntimeException(
                        "WARNING: realtime status is ${realtimeResponse.status} " +
                                "WARNING: daily status is ${dailyResponse.status} " +
                                "WARNING: hourly status is ${hourlyResponse.status} " +
                                "WARNING: hourly status is ${minutelyResponse.status}"
                    )
                )
            }
        }
    }


    private fun <T> fire(context:CoroutineContext,block:suspend()-> Result<T>): LiveData<Result<T>> {
        return liveData(context) {
            val result = try {
                block()
            }catch (e:Exception){
                Result.failure(e)
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
     * 将 UNIX 时间转换成 string 格式的 date
     */
    fun changeUNIXIntoString(unixTime:Long)=WeatherDao.changeUNIXIntoString(unixTime)
}
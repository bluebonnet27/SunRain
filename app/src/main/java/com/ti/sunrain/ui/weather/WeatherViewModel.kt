package com.ti.sunrain.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ti.sunrain.logic.Repository
import com.ti.sunrain.logic.model.PlaceResponse.Location

/**
 * @author: tihon
 * @date: 2020/12/7
 * @description:
 */
class WeatherViewModel:ViewModel() {

    private val locationLiveData = MutableLiveData<Location>()

    var locationLng = ""

    var locationLat = ""

    var placeName = ""

    val weatherLiveData = Transformations.switchMap(locationLiveData){ location->
        Repository.refreshWeather(location.lng,location.lat)
    }

    /**
     * 刷新天气，在viewmodel层
     */
    fun refreshWeather(lng:String,lat:String){
        locationLiveData.value=Location(lng, lat)
    }

    /**
     * Change UNIX time into Real time
     */
    fun changeUNIXIntoString(unixTime:Long) = Repository.changeUNIXIntoString(unixTime)

    /**
     * get AQI color
     */
    fun getAQIColor(aqiValue:Int):String{
        when (aqiValue) {
            in 0..50 -> {
                return "#7cb342"
            }
            in 51..100 -> {
                return "#ffff8d"
            }
            in 101..150 -> {
                return "#f9a825"
            }
            in 151..200 -> {
                return "#e53935"
            }
            in 201..300 -> {
                return "#880e4f"
            }
            else -> {
                return "#212121"
            }
        }
    }
}
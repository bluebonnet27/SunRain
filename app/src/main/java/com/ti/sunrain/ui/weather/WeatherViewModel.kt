package com.ti.sunrain.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ti.sunrain.logic.Repository
import com.ti.sunrain.logic.model.Location

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

    fun refreshWeather(lng:String,lat:String){
        locationLiveData.value=com.ti.sunrain.logic.model.Location(lng, lat)
    }

    /**
     * Change UNIX time into Real time
     */
    fun changeUNIXIntoString(unixTime:Long) = Repository.changeUNIXIntoString(unixTime)
}
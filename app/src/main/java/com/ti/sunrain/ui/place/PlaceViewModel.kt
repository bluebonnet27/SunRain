package com.ti.sunrain.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ti.sunrain.logic.Repository
import com.ti.sunrain.logic.model.PlaceResponse.Place

/**
 * @author: tihon
 * @date: 2020/12/3
 * @description:
 */
class PlaceViewModel:ViewModel() {

    private val searchLiveData = MutableLiveData<String>()

    val placeList = ArrayList<Place>()

    val placeLiveData = Transformations.switchMap(searchLiveData){
        query-> Repository.searchPlaces(query)
    }

    fun searchPlaces(query:String){
        searchLiveData.value = query
    }

    /**
     * 保存地点
     */
    fun savePlace(place:Place) = Repository.savePlace(place)

    /**
     * 读取保存的地点
     */
    fun getSavedPlace() = Repository.getSavedPlace()

    /**
     * 判断地点是否在保存的列表里
     */
    fun isPlaceSaved() = Repository.isPlaceSaved()

}
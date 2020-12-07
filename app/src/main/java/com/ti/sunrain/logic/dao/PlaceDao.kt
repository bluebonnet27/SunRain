package com.ti.sunrain.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.ti.sunrain.SunRainApplication
import com.ti.sunrain.logic.model.Place

/**
 * @author: tihon
 * @date: 2020/12/5
 * @description:
 */
object PlaceDao {

    /**
     * 持久化存储，存储访问的城市
     */
    fun savePlace(place: Place){
        sharedPreferences().edit{
            putString("place",Gson().toJson(place))
        }
    }

    /**
     * 读取城市信息
     */
    fun getSavedPlace():Place{
        val placeJson = sharedPreferences().getString("place","")
        return Gson().fromJson(placeJson,Place::class.java)
    }

    /**
     * 判断城市是否在列表里
     */
    fun isPlaceSaved() = sharedPreferences().contains("place")

    private fun sharedPreferences() = SunRainApplication.context.
            getSharedPreferences("sunny_weather",Context.MODE_PRIVATE)
}
package com.ti.sunrain.logic.network

import com.ti.sunrain.SunRainApplication
import com.ti.sunrain.logic.model.SpecificPlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceSpecificService {
    /**
     * 经纬度反解析城市信息接口
     */
    @GET("v3/?ak=${SunRainApplication.TOKEN_AK}&output=json&location=")
    fun getAddressByLngAndLat(@Query("lng")lng:String,
                              @Query("lat")lat:String): Call<SpecificPlaceResponse>

    @GET("v3/?ak=${SunRainApplication.TOKEN_AK}&output=json")
    fun getAddressByLocation(@Query("location")location:String):Call<SpecificPlaceResponse>
}
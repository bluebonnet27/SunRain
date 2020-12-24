package com.ti.sunrain.logic.model

import com.google.gson.annotations.SerializedName

/**
 * @author: tihon
 * @date: 2020/12/3
 * @description:
 */

/**
 * {
    "status": "ok",
    "query": "北京",
    "places":
    [
        {
            "id": "ChIJuSwU55ZS8DURiqkPryBWYrk",
            "location":
            {
                "lat": 39.9041999,
                "lng": 116.4073963
            },
        "place_id": "g-ChIJuSwU55ZS8DURiqkPryBWYrk",
        "name": "北京市",
        "formatted_address": "中国北京市"
        }
    ]
    }
 */
/**
 * 应答地点类，从全局 context 来，作为 mvvm 的 model 层
 */
data class PlaceResponse(val status:String,val places : List<Place>){

    /**
     * 名称，定位类，具体地址名字
     */
    data class Place(val name:String,val location: Location,
                     @SerializedName("formatted_address") val address:String)

    /**
     * 经度，纬度
     */
    data class Location(val lng:String,val lat:String)
}

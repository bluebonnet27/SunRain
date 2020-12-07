package com.ti.sunrain.logic.model

import com.google.gson.annotations.SerializedName

/**
 * @author: tihon
 * @date: 2020/12/6
 * @description:
 */
/**
 * 彩云天气实时返回接口2.5
 */
data class RealtimeResponse(val status:String,
                            val result:Result){
    /**
     * Result,父类为RealtimeResponse
     */
    data class Result(val realtime:Realtime)

    /**
     * Realtime,父类为Result,顺序以官方文档为准
     */
    data class Realtime(val temperature:Float,//温度
                        @SerializedName("apparent_temperature")val apparentTemperature:Float,//体感温度
                        val humidity:Float,//湿度
                        val wind:Wind,//风
                        val skycon:String,//主要天气现象
                        @SerializedName("life_index")val lifeIndex:LifeIndex,
                        @SerializedName("air_quality")val airQuality:AirQuality
    )

    /**
     * "wind": {
        "speed": 15.12,
        "direction": 213.0
        },
     */
    data class Wind(val speed:Float,val direction:Float)

    /**
     * "life_index": {
    "ultraviolet": {
    "index": 1.0,
    "desc": "很弱"
    },
    "comfort": {
    "index": 11,
    "desc": "刺骨的冷"
    }
    }
     */
    data class LifeIndex(val comfort:Comfort,val ultraviolet:Ultraviolet)

    /**
     * "comfort": {
    "index": 11,
    "desc": "刺骨的冷"
    }
     */
    data class Comfort(val index:Float,val desc:String)

    /**
     * "ultraviolet": {
    "index": 1.0,
    "desc": "很弱"
    },
     */
    data class Ultraviolet(val index: Float,val desc: String)

    /**
     * "air_quality": {
    "pm25": 15,
    "pm10": 0,
    "o3": 0,
    "so2": 0,
    "no2": 0,
    "co": 0,
    "aqi": {
    "chn": 26,
    "usa": 0
    },
    "description": {
    "usa": "",
    "chn": "优"
    }
    },
     */
    data class AirQuality(val pm25:Float,val pm10:Float,val o3:Float,
                            val no2:Float,val so2:Float,val co:Float,
                            val aqi:AQI,
                            val description:DescriptionWeather)

    /**
     * "aqi": {
    "chn": 26,
    "usa": 0
    },
     */
    data class AQI(val chn:Float)

    /**
     * "description": {
    "usa": "",
    "chn": "优"
    }
     */
    data class DescriptionWeather(val chn:String)

}
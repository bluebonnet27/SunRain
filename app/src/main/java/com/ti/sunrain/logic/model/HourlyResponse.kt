package com.ti.sunrain.logic.model

/**
 * @author: tihon
 * @date: 2020/12/20
 * @description:
 */
data class HourlyResponse(val status:String, val server_time:Long,val result:Result){

    data class Result(val hourly:Hourly){

        data class Hourly(val description:String,
                          val precipitation:List<Precipitation>,
                          val temperature:List<Temperature>,
                          val wind:List<Wind>,
                          val skycon:List<Skycon>){

            data class Precipitation(val datetime:String,val value:Float)

            data class Temperature(val datetime:String,val value:Float)

            data class Wind(val datetime: String,
                            val speed:Float,
                            val direction:Float)

            data class Skycon(val datetime: String,val value: String)
        }
    }
}
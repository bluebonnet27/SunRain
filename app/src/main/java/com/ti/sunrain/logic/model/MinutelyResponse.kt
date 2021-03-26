package com.ti.sunrain.logic.model

/**
 * @author: tihon
 * @date: 2021/3/25
 * @description:
 */
class MinutelyResponse(val status:String,val result: Result) {

    data class Result(val minutely:Minutely){

        data class Minutely(val status: String,
                            val datasource:String,
                            val precipitation_2h:List<Float>,
                            val precipitation:List<Float>,
                            val probability:List<Float>,
                            val description:String)
    }
}
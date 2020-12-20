package com.ti.sunrain.logic.model

/**
 * @author: tihon
 * @date: 2020/12/20
 * @description:
 */
/**
 * 这个类的存在意义是解决recyclerview对实体类的强要求问题，后期在找到更好的办法解决问题后会删掉
 */
data class HourlyItem (val skycon: HourlyResponse.Result.Hourly.Skycon,
                       val temperature:HourlyResponse.Result.Hourly.Temperature,
                       val wind:HourlyResponse.Result.Hourly.Wind)
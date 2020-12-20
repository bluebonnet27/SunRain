package com.ti.sunrain.logic.model

/**
 * @author: tihon
 * @date: 2020/12/7
 * @description:
 */
data class Weather(val realtime:RealtimeResponse.Realtime,
                   val daily:DailyResponse.Daily,
                   val realtimeResponse:RealtimeResponse,
                   val wind: RealtimeResponse.Wind,
                   val hourly:HourlyResponse.Result.Hourly)
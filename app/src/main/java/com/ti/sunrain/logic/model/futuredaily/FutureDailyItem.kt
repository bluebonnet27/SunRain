package com.ti.sunrain.logic.model.futuredaily

import com.ti.sunrain.logic.model.DailyResponse

data class FutureDailyItem(val skyconDay:DailyResponse.Skycon,
                           val skyconNight:DailyResponse.Skycon,
                           val tempTwo:DailyResponse.Temperature,
                           val wind:DailyResponse.WindDesc,
                           val airQuality: DailyResponse.AQIDesc)
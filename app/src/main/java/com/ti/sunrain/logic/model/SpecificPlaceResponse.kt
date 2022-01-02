package com.ti.sunrain.logic.model

import com.google.gson.annotations.SerializedName

/**
 * {
"status": 0,
"result": {
"location": {
"lng": 121.99999999999993,
"lat": 40.99999995725079
},
"formatted_address": "辽宁省盘锦市大洼区",
"business": "",
"addressComponent": {
"country": "中国",
"country_code": 0,
"country_code_iso": "CHN",
"country_code_iso2": "CN",
"province": "辽宁省",
"city": "盘锦市",
"city_level": 2,
"district": "大洼区",
"town": "",
"town_code": "",
"distance": "",
"direction": "",
"adcode": "211104",
"street": "",
"street_number": ""
},
"pois": [],
"roads": [],
"poiRegions": [],
"sematic_description": "",
"cityCode": 228
}
}
 */
class SpecificPlaceResponse (val status:Int,
                             val result:Result){
    class Result(@SerializedName("formatted_address")val detailedAddress:String,
                      val business:String)
}
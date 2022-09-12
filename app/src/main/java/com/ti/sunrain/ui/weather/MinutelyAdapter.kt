package com.ti.sunrain.ui.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ti.sunrain.R
import com.ti.sunrain.logic.model.MinutelyItem

/**
 * @author: tihongsheng
 * @date: 2021/3/25
 * @description:
 */
class MinutelyAdapter(private val minutelyItemList: List<MinutelyItem>) :
    RecyclerView.Adapter<MinutelyAdapter.ViewHolder>(){

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val minutelyTimeText : TextView = view.findViewById(R.id.minutelyTimeText)
        val minutelyTimeIconImage : ImageView = view.findViewById(R.id.minutelyTimeIconImage)
        val minutelyTimeDescriptionText : TextView =
            view.findViewById(R.id.minutelyTimeDescriptionText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_minutely_rain,
            parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val minutelyItem = minutelyItemList[position]
        //fill in
        //time
        val minutes60 = mutableListOf(1)
        for(i in 2..60)
            minutes60.add(i)
        holder.minutelyTimeText.text = minutes60[position].toString() + "分钟"
        //icon
        holder.minutelyTimeIconImage.setImageResource(getRainIconDesc(minutelyItem.timeDescription))
        //time description
        holder.minutelyTimeDescriptionText.text = getRainName(minutelyItem.timeDescription)
    }

    override fun getItemCount(): Int {
        return minutelyItemList.size
    }

    fun getRainIconDesc(rainPrecipitation:Float):Int{
        return if(rainPrecipitation < 0.031){
            R.drawable.ic_clear_day
        }else if(rainPrecipitation >= 0.031 && rainPrecipitation < 0.25){
            R.drawable.ic_light_rain
        }else if(rainPrecipitation >= 0.25 && rainPrecipitation < 0.35){
            R.drawable.ic_moderate_rain
        }else if(rainPrecipitation >= 0.35 && rainPrecipitation < 0.48){
            R.drawable.ic_heavy_rain
        }else if(rainPrecipitation >= 0.48){
            R.drawable.ic_storm_rain
        }else{
            R.drawable.ic_cloudy
        }
    }

    private fun getRainName(rainPrecipitation:Float):String{
        return if(rainPrecipitation < 0.031){
            "无雨／雪"
        }else if(rainPrecipitation >= 0.031 && rainPrecipitation < 0.25){
            "小雨／雪"
        }else if(rainPrecipitation >= 0.25 && rainPrecipitation < 0.35){
            "中雨／雪"
        }else if(rainPrecipitation >= 0.35 && rainPrecipitation < 0.48){
            "大雨／雪"
        }else if(rainPrecipitation >= 0.48){
            "暴雨／雪"
        }else{
            "ERROR"
        }
    }
}
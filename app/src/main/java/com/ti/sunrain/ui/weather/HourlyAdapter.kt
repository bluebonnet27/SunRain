package com.ti.sunrain.ui.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ti.sunrain.R
import com.ti.sunrain.logic.model.HourlyItem
import com.ti.sunrain.logic.model.getSky
import com.ti.sunrain.logic.model.getWindIcon
import com.ti.sunrain.logic.model.getWindSpeed
import kotlinx.android.synthetic.main.item_hourly.view.*

/**
 * @author: tihon
 * @date: 2020/12/20
 * @description:
 */
class HourlyAdapter(private val hourlyItemList:List<HourlyItem>) :
    RecyclerView.Adapter<HourlyAdapter.ViewHolder>(){

    inner class ViewHolder(view:View) : RecyclerView.ViewHolder(view){
        val skyconImage : ImageView = view.findViewById(R.id.skyconHourlyItem)
        val tempText:TextView = view.findViewById(R.id.tempHourlyItem)
        val windImage : ImageView = view.findViewById(R.id.windHourlyItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hourly,
            parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hourlyItem = hourlyItemList[position]
        //分别数据注入，需要一些转换函数辅助
        //skyconImage
        val sky = getSky(hourlyItem.skycon.value)
        val skyconImageSource = sky.weather_icon
        holder.skyconImage.setImageResource(skyconImageSource)
        //tempText
        holder.tempText.text = hourlyItem.temperature.value.toString()
        //windImage
        val windSpeed = hourlyItem.wind.speed
        val windImageResource = getWindIcon(getWindSpeed(windSpeed))
        holder.windImage.setImageResource(windImageResource)
    }

    override fun getItemCount(): Int {
        return hourlyItemList.size
    }
}
package com.ti.sunrain.ui.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ti.sunrain.R
import com.ti.sunrain.SunRainApplication
import com.ti.sunrain.logic.model.dayforecast.DayForecastItem
import com.ti.sunrain.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author: tihon
 * @date: 2021/4/3
 * @description:
 */
class DayForecastAdapter(val dayForecastList:List<DayForecastItem>):
    RecyclerView.Adapter<DayForecastAdapter.ViewHolder>(){

    private lateinit var onItemClickListener: OnItemClickListener

    inner class ViewHolder(view:View) : RecyclerView.ViewHolder(view){
        val dateInfoText:TextView = view.findViewById(R.id.dateInfo)
        val skyconImg:ImageView = view.findViewById(R.id.skyIcon)
        val skyInfoText:TextView = view.findViewById(R.id.skyInfo)
        val temperatureInfoText:TextView = view.findViewById(R.id.temperatureInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.forecast_item,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //数据填充
        val dayForecastItem = dayForecastList[position]

        //date
        val dateFormatValue = SunRainApplication.settingsPreference
            .getString("forecastDateFormat_list","0")

        if(dateFormatValue=="0"){
            holder.dateInfoText.text =
                transferWeekDayToLocalWeekDay(transferDateToWeekDay(dayForecastItem.skycon.date),0)
        }else{
            val dateOrigin = dayForecastItem.skycon.date
            val simpleDateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
            holder.dateInfoText.text = simpleDateFormat.format(dateOrigin)
        }

        //skyconImg
        val sky = getSky(dayForecastItem.skycon.value)
        holder.skyconImg.setImageResource(sky.weather_icon)

        //skyInfoText
        holder.skyInfoText.text = sky.info

        //temperatureText
        val tempText = "${dayForecastItem.temp.min.toInt()}~${dayForecastItem.temp.max.toInt()}℃"
        holder.temperatureInfoText.text = tempText

        //点击
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(holder.itemView,position,
                holder.dateInfoText.text.toString())
        }
    }

    override fun getItemCount(): Int {
        return dayForecastList.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.onItemClickListener = listener
    }

    private fun transferDateToWeekDay(originDate:Date):Int{
        val weekdays = listOf(0,1,2,3,4,5,6)
        val calendar = Calendar.getInstance()
        calendar.time = originDate
        var weekIndex = calendar.get(Calendar.DAY_OF_WEEK)-1

        if (weekIndex<0) weekIndex=0

        return weekdays[weekIndex]
    }

    private fun transferWeekDayToLocalWeekDay(index:Int, language:Int):String{
        val weekDaysCHS = listOf("星期日","星期一","星期二","星期三","星期四","星期五","星期六")
        val weekdaysENG = listOf("Sun","Mon","Tue","Wed","Thu","Fri","Sat")
        return if(language==0){
            weekDaysCHS[index]
        }else if(language==1){
            weekdaysENG[index]
        }else{
            "ERROR"
        }
    }

    interface OnItemClickListener {
        //RecyclerView的点击事件，将信息回调给view
        fun onItemClick(view:View,position:Int,weekday:String)
    }
}


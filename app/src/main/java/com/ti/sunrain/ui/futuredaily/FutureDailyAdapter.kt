package com.ti.sunrain.ui.futuredaily

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ti.sunrain.R
import com.ti.sunrain.SunRainApplication
import com.ti.sunrain.logic.model.futuredaily.FutureDailyItem
import com.ti.sunrain.logic.model.getSky
import com.ti.sunrain.logic.model.getWindIcon
import com.ti.sunrain.logic.model.getWindSpeed
import java.text.SimpleDateFormat
import java.util.*

class FutureDailyAdapter(private val futureDailyList:List<FutureDailyItem>):
    RecyclerView.Adapter<FutureDailyAdapter.ViewHolder>() {

    private lateinit var onItemClickListener: OnItemClickListener
    private val context = SunRainApplication.context

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val dateText:TextView = view.findViewById(R.id.futureDailyDate)

        val dayImg:ImageView = view.findViewById(R.id.futureDailyDaySkyconImg)
        val dayInfo:TextView = view.findViewById(R.id.futureDailyDaySkyconInfo)

        val tempMax:TextView = view.findViewById(R.id.futureDailyMaxTemp)
        val tempMin:TextView = view.findViewById(R.id.futureDailyMinTemp)

        val nightImg:ImageView = view.findViewById(R.id.futureDailyNightSkyconImg)
        val nightInfo:TextView = view.findViewById(R.id.futureDailyNightSkyconInfo)

        val windImg:ImageView = view.findViewById(R.id.futureDailyWindImg)
        val airDesc:TextView = view.findViewById(R.id.futureDailyAirDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_future_daily,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //从list中取对象
        val futureDailyItem = futureDailyList[position]

        //日期数据填充
        val dateFormatValue = SunRainApplication.settingsPreference
            .getString("forecastDateFormat_list","0")

        if(dateFormatValue=="0"){
            holder.dateText.text =
                transferWeekDayToWeekDay(transferDateToWeekDay(futureDailyItem.skyconDay.date))
        }else{
            val dateOrigin = futureDailyItem.skyconDay.date
            val simpleDateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
            holder.dateText.text = simpleDateFormat.format(dateOrigin)
        }

        //白天天气图标，描述
        val skyDay = getSky(futureDailyItem.skyconDay.value)
        holder.dayImg.setImageResource(skyDay.weather_icon)

        holder.dayInfo.text = skyDay.info

        //最高温度和最低温度，暂时以摄氏度为标准
        holder.tempMax.text = String.format(context.resources.getString(
            R.string.futureDailyAdapter_holder_tempMax_text),futureDailyItem.tempTwo.max)
        holder.tempMin.text = "${futureDailyItem.tempTwo.min}℃"

        //夜晚天气图标，描述
        val skyNight = getSky(futureDailyItem.skyconNight.value)
        holder.nightImg.setImageResource(skyNight.weather_icon)

        holder.nightInfo.text = skyNight.info

        //风力
        val windSpeed = futureDailyItem.wind.speed
        holder.windImg.setImageResource(getWindIcon(getWindSpeed(windSpeed)))

        //空气指数
        val airDesc = futureDailyItem.airQuality.avg.chn.toInt()
        holder.airDesc.text = getAQIDesc(airDesc)

        //点击回调监听器
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(holder.itemView,position,holder.dateText.text.toString())
        }
    }

    override fun getItemCount(): Int {
        return futureDailyList.size
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

    private fun transferWeekDayToWeekDay(index:Int):String{
        val weekDays = listOf(context.getString(R.string.textview_dayforecastadapter_sun),
            context.getString(R.string.textview_dayforecastadapter_mon),
            context.getString(R.string.textview_dayforecastadapter_tue),
            context.getString(R.string.textview_dayforecastadapter_wed),
            context.getString(R.string.textview_dayforecastadapter_thu),
            context.getString(R.string.textview_dayforecastadapter_fri),
            context.getString(R.string.textview_dayforecastadapter_sat))
        return weekDays[index]
    }

    private fun getAQIDesc(aqiValue:Int):String{
        when (aqiValue) {
            in 0..50 -> {
                return "优"
            }
            in 51..100 -> {
                return "良"
            }
            in 101..150 -> {
                return "轻度污染"
            }
            in 151..200 -> {
                return "中度污染"
            }
            in 201..300 -> {
                return "重度污染"
            }
            else -> {
                return "严重污染"
            }
        }
    }

    interface OnItemClickListener{
        //RecyclerView的点击事件，将信息回调给view
        fun onItemClick(view:View,position:Int,weekday:String)
    }
}
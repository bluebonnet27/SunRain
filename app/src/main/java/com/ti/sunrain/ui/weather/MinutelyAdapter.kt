package com.ti.sunrain.ui.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ti.sunrain.R
import com.ti.sunrain.SunRainApplication
import com.ti.sunrain.logic.model.MinutelyItem

/**
 * @author: tihon
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
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val minutelyItem = minutelyItemList[position]
        //fill in
        //time
        //val minutes60 = arrayOf(1..60)
        holder.minutelyTimeText.text = "test" //minutes60[position].toString()
        //icon
        if(minutelyItem.timeDescription > 0){
            holder.minutelyTimeIconImage.setImageResource(R.drawable.ic_light_rain)
        }else{
            holder.minutelyTimeIconImage.setImageResource(R.drawable.ic_cloudy)
        }
        //time description
        holder.minutelyTimeDescriptionText.text = minutelyItem.timeDescription.toString()
    }

    override fun getItemCount(): Int {
        return minutelyItemList.size
    }
}
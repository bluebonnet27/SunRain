package com.ti.sunrain.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ti.sunrain.R
import com.ti.sunrain.logic.model.PlaceResponse.Place
import com.ti.sunrain.ui.weather.WeatherActivity
import kotlinx.android.synthetic.main.activity_weather.*

/**
 * @author: tihon
 * @date: 2020/12/3
 * @description:
 */
class PlaceAdapter(private val fragment: PlaceFragment,
                   private val placeList: List<Place>):
                    RecyclerView.Adapter<PlaceAdapter.ViewHolder>(){

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val placeName : TextView = view.findViewById(R.id.placeName)
        val placeAddress : TextView = view.findViewById(R.id.placeAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item,parent,false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val place = placeList[position]

            val activity = fragment.activity
            if(activity is WeatherActivity){
                activity.drawerLayout.closeDrawers()

                activity.viewModel.locationLng = place.location.lng
                activity.viewModel.locationLat = place.location.lat
                activity.viewModel.placeName = place.address

                activity.refreshWeather()
            }else{
                val intent = Intent(parent.context, WeatherActivity::class.java).apply {
                    putExtra("location_lng",place.location.lng)
                    putExtra("location_lat",place.location.lat)
                    putExtra("place_name",place.name)
                }
                fragment.startActivity(intent)
                activity?.finish()
            }
            fragment.viewModel.savePlace(place)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.address
        // animation
        holder.itemView.animation = AnimationUtils.loadAnimation(holder.itemView.context,
            R.anim.animation_res)
    }

    override fun getItemCount(): Int {
        return placeList.size
    }
}
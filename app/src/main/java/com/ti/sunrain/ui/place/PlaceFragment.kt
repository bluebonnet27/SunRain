package com.ti.sunrain.ui.place

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ti.sunrain.MainActivity
import com.ti.sunrain.R
import com.ti.sunrain.ui.weather.WeatherActivity
import kotlinx.android.synthetic.main.fragment_place.*
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.permissionx.guolindev.PermissionX
import com.ti.sunrain.SunRainApplication
import com.ti.sunrain.logic.model.SpecificPlaceResponse
import com.ti.sunrain.logic.network.PlaceSpecificService
import kotlinx.android.synthetic.main.activity_weather.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.jar.Manifest


/**
 * @author: tihon
 * @date: 2020/12/3
 * @description:
 */
class PlaceFragment:Fragment() {

    val viewModel by lazy {
        ViewModelProviders.of(this).get(PlaceViewModel::class.java)
    }

    private lateinit var adapter: PlaceAdapter

    private val locationManager = SunRainApplication.context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_place,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //judge ahead
        if(activity is MainActivity && viewModel.isPlaceSaved()){
            val place = viewModel.getSavedPlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng",place.location.lng)
                putExtra("location_lat",place.location.lat)
                putExtra("place_name",place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }

        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this,viewModel.placeList)
        recyclerView.adapter = adapter

        searchPlaceEdit.addTextChangedListener { editable ->
            val content = editable.toString()
            if(content.isNotEmpty()){
                viewModel.searchPlaces(content)
            }else{
                recyclerView.visibility = View.GONE
                bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }

        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer { result ->
            val places = result.getOrNull()
            if(places!=null){
                recyclerView.visibility = View.VISIBLE
                bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            }else{
                Toast.makeText(activity, "没能查询到任何地点呢！", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        
        searchPlaceLoactionBtn.setOnClickListener { view->
            PermissionX.init(this)
                .permissions(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                .request{allGranted, grantedList, deniedList ->  
                    if(allGranted){
                        try{
                            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            if (location != null) {
                                val activity = this.activity

                                val lng = location.longitude.toString()
                                val lat = location.latitude.toString()

                                val locationString = "$lat,$lng"

                                if(activity is WeatherActivity){
                                    activity.drawerLayout.closeDrawers()

                                    activity.viewModel.locationLng = lng
                                    activity.viewModel.locationLat = lat

                                    val retrofit = Retrofit.Builder()
                                        .baseUrl("https://api.map.baidu.com/reverse_geocoding/")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build()
                                    val placeSpecificService = retrofit.create(PlaceSpecificService::class.java)
                                    placeSpecificService.getAddressByLocation(locationString).enqueue(object :Callback<SpecificPlaceResponse>{
                                        override fun onResponse(
                                            call: Call<SpecificPlaceResponse>,
                                            response: Response<SpecificPlaceResponse>
                                        ) {
                                            val specificPlaceResponse = response.body()
                                            if (specificPlaceResponse != null) {
                                                activity.viewModel.placeName = specificPlaceResponse.result.detailedAddress
                                                Toast.makeText(SunRainApplication.context,
                                                    specificPlaceResponse.result.detailedAddress, Toast.LENGTH_SHORT).show()
                                            }else{
                                                activity.viewModel.placeName = "地址缺失！"
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<SpecificPlaceResponse>,
                                            t: Throwable
                                        ) {
                                            t.stackTrace
                                        }
                                    })

                                    activity.refreshWeather()
                                }else{
                                    val intent = Intent(this.context, WeatherActivity::class.java).apply {
                                        putExtra("location_lng",lng)
                                        putExtra("location_lat",lat)

                                        val retrofit = Retrofit.Builder()
                                            .baseUrl("https://api.map.baidu.com/reverse_geocoding/")
                                            .addConverterFactory(GsonConverterFactory.create())
                                            .build()
                                        val placeSpecificService = retrofit.create(PlaceSpecificService::class.java)
                                        placeSpecificService.getAddressByLocation(locationString).enqueue(object :Callback<SpecificPlaceResponse>{
                                            override fun onResponse(
                                                call: Call<SpecificPlaceResponse>,
                                                response: Response<SpecificPlaceResponse>
                                            ) {
                                                val specificPlaceResponse = response.body()
                                                if (specificPlaceResponse != null) {
                                                    putExtra("place_name",specificPlaceResponse.result.detailedAddress)
                                                    Toast.makeText(SunRainApplication.context,
                                                        specificPlaceResponse.result.detailedAddress, Toast.LENGTH_SHORT).show()
                                                }else{
                                                    putExtra("place_name","地址缺失！")
                                                }
                                            }

                                            override fun onFailure(
                                                call: Call<SpecificPlaceResponse>,
                                                t: Throwable
                                            ) {
                                                t.stackTrace
                                            }
                                        })
                                    }
                                    startActivity(intent)
                                    activity?.finish()
                                }
                            }
                        }catch (e:SecurityException){
                            e.stackTrace
                        }
                    }else{
                        Toast.makeText(this.requireActivity(), "你拒绝了权限！", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
package com.example.weatherforecast.location.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.DayOfWeekLayoutBinding
import com.example.weatherforecast.model.WeatherItem
import java.util.*


class DaysOfWeekAdapter(private val context: Context?): ListAdapter<WeatherItem, DaysOfWeekAdapter.LocationViewHolder>(LocationDiffUtils()){

    private lateinit var binding: DayOfWeekLayoutBinding
    private lateinit var appUnits: String
    private lateinit var windUnit: String
    private var preferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DayOfWeekLayoutBinding.inflate(inflater, parent, false)
        return LocationViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        getPreferences()
        setUnits()
        val currentLocation = getItem(position)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = currentLocation.dt * 1000
        }
        val language = preferences?.getString("language", "").toString()
        val locale: Locale = if(language == "ar") {
            Locale("ar")
        }else{
            Locale.ENGLISH
        }
        val dayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale)

        holder.binding.tempTV.text = currentLocation.main.temp.toString()
        holder.binding.tempUnit.text = appUnits
        holder.binding.dayOfWeekTV.text = dayName
        holder.binding.windSpeed.text = currentLocation.wind.speed.toString()+ " "+ windUnit

        /*if (context != null) {
            Glide.with(context)
                .load("https://openweathermap.org/img/w/${currentLocation.weather[0].icon}.png")
                .into(holder.binding.weekIcon)
        }*/
        val char = (currentLocation.weather[0].icon)[2]
        val imageName: String =  char + currentLocation.weather[0].icon

        val imageResource = context?.resources?.getIdentifier(imageName, "drawable", context.packageName)

        if (imageResource != null) {
            holder.binding.weekIcon.setImageResource(imageResource)
        }

    }

    private fun getPreferences(){
        preferences = context?.getSharedPreferences("pref", Context.MODE_PRIVATE)
        editor = preferences?.edit()
        appUnits = preferences?.getString("unit", "").toString()
    }
    private fun setUnits(){
        when (appUnits) {
            "" -> {
                appUnits = "K"
                windUnit = context?.getString(R.string.ms).toString()
            }
            "metric" -> {
                appUnits = "°C"

                windUnit = context?.getString(R.string.ms).toString()
            }
            else -> {
                appUnits = "°F"
                windUnit = context?.getString(R.string.mh).toString()
            }
        }
    }
    class LocationViewHolder(var binding: DayOfWeekLayoutBinding) : RecyclerView.ViewHolder(binding.root)

}

class LocationDiffUtils : DiffUtil.ItemCallback<WeatherItem>(){
    override fun areItemsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
        return oldItem.weather[0].description == newItem.weather[0].description
    }
    override fun areContentsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
        return oldItem == newItem
    }
}
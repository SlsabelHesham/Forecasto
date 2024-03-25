package com.example.weatherforecast.location.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.databinding.DayOfWeekLayoutBinding
import com.example.weatherforecast.model.WeatherItem
import java.util.*


class DaysOfWeekAdapter(private val context: Context?): ListAdapter<WeatherItem, DaysOfWeekAdapter.LocationViewHolder>(LocationDiffUtils()){

    private lateinit var binding: DayOfWeekLayoutBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DayOfWeekLayoutBinding.inflate(inflater, parent, false)
        return LocationViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val currentLocation = getItem(position)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = currentLocation.dt * 1000
        }
        val dayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH)

        holder.binding.tempTV.text = currentLocation.main.temp.toString()
        holder.binding.dayOfWeekTV.text = dayName
        holder.binding.windSpeed.text = currentLocation.wind.speed.toString()

        if (context != null) {
            Glide.with(context)
                .load("https://openweathermap.org/img/w/${currentLocation.weather[0].icon}.png")
                .into(holder.binding.weekIcon)
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
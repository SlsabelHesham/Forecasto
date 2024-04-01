package com.example.weatherforecast.location.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.databinding.HoursOfDayLayoutBinding
import com.example.weatherforecast.model.WeatherItem
import java.util.*


class HoursOfDayAdapter(private val context: Context?): ListAdapter<WeatherItem, HoursOfDayAdapter.LocationViewHolder>(LocationDiffUtil()){

    private lateinit var binding: HoursOfDayLayoutBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = HoursOfDayLayoutBinding.inflate(inflater, parent, false)
        return LocationViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val currentLocation = getItem(position)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = currentLocation.dt * 1000
        }
        val hour = calendar.get(Calendar.HOUR)
        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"


        holder.binding.tempTV.text = currentLocation.main.temp.toString()
        holder.binding.hourTV.text = "$hour $amPm"

        /*
        if (context != null) {
            Glide.with(context)
                .load("https://openweathermap.org/img/w/${currentLocation.weather[0].icon}.png")
                .into(holder.binding.imageView)
        }*/
        val char = (currentLocation.weather[0].icon)[2]
        val imageName: String =  char + currentLocation.weather[0].icon

        val imageResource = context?.resources?.getIdentifier(imageName, "drawable", context.packageName)

        if (imageResource != null) {
            holder.binding.imageView.setImageResource(imageResource)
        }
    }

    class LocationViewHolder(var binding: HoursOfDayLayoutBinding) : RecyclerView.ViewHolder(binding.root)

}

class LocationDiffUtil : DiffUtil.ItemCallback<WeatherItem>(){
    override fun areItemsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
        return oldItem.weather[0].description == newItem.weather[0].description
    }
    override fun areContentsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
        return oldItem == newItem
    }
}
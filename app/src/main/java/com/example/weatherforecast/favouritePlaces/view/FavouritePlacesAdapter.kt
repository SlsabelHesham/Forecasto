package com.example.weatherforecast.favouritePlaces.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.databinding.FavPlacesLayoutBinding
import com.example.weatherforecast.model.LatLng


class FavouritePlacesAdapter(private val listener: OnDeletePlaceClickListener): ListAdapter<LatLng, FavouritePlacesAdapter.PlacesViewHolder>(PlacesDiffUtil()){

    private lateinit var binding: FavPlacesLayoutBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = FavPlacesLayoutBinding.inflate(inflater, parent, false)
        return PlacesViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PlacesViewHolder, position: Int) {
        val currentPlace = getItem(position)
        holder.binding.tvTitle.text = "Latitude: " + currentPlace.latitude
        holder.binding.tvDescription.text = "Longitude: " + currentPlace.longitude
        holder.binding.removeBtn.setOnClickListener {
            listener.onDeletePlaceClick(currentPlace)
        }
    }

    class PlacesViewHolder(var binding: FavPlacesLayoutBinding) : RecyclerView.ViewHolder(binding.root)

}

class PlacesDiffUtil : DiffUtil.ItemCallback<LatLng>(){
    override fun areItemsTheSame(oldItem: LatLng, newItem: LatLng): Boolean {
        return oldItem.latitude == newItem.latitude
    }
    override fun areContentsTheSame(oldItem: LatLng, newItem: LatLng): Boolean {
        return oldItem == newItem
    }
}
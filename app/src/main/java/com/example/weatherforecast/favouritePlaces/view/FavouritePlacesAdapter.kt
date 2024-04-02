package com.example.weatherforecast.favouritePlaces.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FavPlacesLayoutBinding
import com.example.weatherforecast.model.FavouriteLocation

class FavouritePlacesAdapter(val context: Context): ListAdapter<FavouriteLocation, FavouritePlacesAdapter.PlacesViewHolder>(PlacesDiffUtil()){

    private lateinit var binding: FavPlacesLayoutBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = FavPlacesLayoutBinding.inflate(inflater, parent, false)
        return PlacesViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PlacesViewHolder, position: Int) {
        val currentPlace = getItem(position)
        holder.binding.cityNameTV.text = currentPlace.cityName
        holder.binding.addressTV.text = currentPlace.address
        holder.binding.view3.setOnClickListener{
            if (isInternetEnabled()) {
                navigateToLocationDetails(currentPlace)
            } else {
                Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkCallback = object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        if (isInternetEnabled()) {
                            navigateToLocationDetails(currentPlace)
                            connectivityManager.unregisterNetworkCallback(this)
                        }
                    }
                }
                connectivityManager.registerDefaultNetworkCallback(networkCallback)
            }
        }

    }
    private fun isInternetEnabled(): Boolean{
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
    private fun navigateToLocationDetails(currentPlace: FavouriteLocation){
        val bundle = Bundle()
        bundle.putDouble("latitude", currentPlace.latitude)
        bundle.putDouble("longitude", currentPlace.longitude)
        val navController =
            Navigation.findNavController((context as Activity), R.id.fragmentNavHost)
        navController.navigate(R.id.action_favouritesFragment_to_currentLocation, bundle)
    }

    class PlacesViewHolder(var binding: FavPlacesLayoutBinding) : RecyclerView.ViewHolder(binding.root)


    /*override fun getItem(position: Int): FavouriteLocation {
        return items[position]
    }

     */
}

class PlacesDiffUtil : DiffUtil.ItemCallback<FavouriteLocation>(){
    override fun areItemsTheSame(oldItem: FavouriteLocation, newItem: FavouriteLocation): Boolean {
        return oldItem.latitude == newItem.latitude
    }
    override fun areContentsTheSame(oldItem: FavouriteLocation, newItem: FavouriteLocation): Boolean {
        return oldItem == newItem
    }
}
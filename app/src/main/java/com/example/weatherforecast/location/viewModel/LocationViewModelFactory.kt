package com.example.weatherforecast.location.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.model.LocationRepository

@Suppress("UNCHECKED_CAST")
class LocationViewModelFactory (private val _repo: LocationRepository, private val latitude: Double, private val longitude: Double, private val units: String, private val lang: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            LocationViewModel(_repo , latitude , longitude, units, lang) as T
        } else {
            throw IllegalArgumentException("ViewModel Class not found")
        }
    }
}
package com.example.weatherforecast.favouritePlaces.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.model.LocationRepository


@Suppress("UNCHECKED_CAST")
class FavouritePlacesViewModelFactory (private val _repo: LocationRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavouritePlacesViewModel::class.java)) {
            FavouritePlacesViewModel(_repo) as T
        } else {
            throw IllegalArgumentException("ViewModel Class not found")
        }
    }
}
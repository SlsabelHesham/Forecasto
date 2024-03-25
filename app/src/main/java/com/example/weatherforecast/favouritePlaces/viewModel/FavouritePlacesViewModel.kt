package com.example.weatherforecast.favouritePlaces.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.LatLng
import com.example.weatherforecast.model.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavouritePlacesViewModel(private val _irepo: LocationRepository) : ViewModel() {
    init {
        getStoredPlaces()
    }
    private var _places: MutableLiveData<List<LatLng>> = MutableLiveData<List<LatLng>>()
    val places: LiveData<List<LatLng>> = _places

    fun deletePlace(latLng: LatLng){
        viewModelScope.launch(Dispatchers.IO) {
            _irepo.deletePlace(latLng)
            getStoredPlaces()
        }
    }

    private fun getStoredPlaces() {
        viewModelScope.launch(Dispatchers.IO) {
            val placesList = _irepo.getStoredPlaces()
            _places.postValue(placesList)
        }
    }
}
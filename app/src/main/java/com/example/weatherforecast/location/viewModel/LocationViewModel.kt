package com.example.weatherforecast.location.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationViewModel(private val _irepo: LocationRepository, latitude: Double, longitude: Double) : ViewModel() {
    init {
        getLocationDetails(latitude , longitude)
    }
    private var _location: MutableLiveData<WeatherApiResponse> = MutableLiveData<WeatherApiResponse>()
    val location: LiveData<WeatherApiResponse> = _location

    fun insertPlace(latLng: LatLng){
        viewModelScope.launch(Dispatchers.IO) {
            _irepo.insertPlace(latLng)
        }
    }

    private fun getLocationDetails(latitude: Double , longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val locationDetails = _irepo.getLocationDetails(latitude , longitude)
            _location.postValue(locationDetails)
        }
    }
}
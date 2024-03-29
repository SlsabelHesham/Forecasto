package com.example.weatherforecast.location.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationViewModel(private val _irepo: LocationRepository, latitude: Double, longitude: Double, units: String, lang: String) : ViewModel() {
    init {
        getLocationDetails(latitude , longitude, units, lang)
    }
    private var _location: MutableLiveData<WeatherApiResponse> = MutableLiveData<WeatherApiResponse>()
    val location: LiveData<WeatherApiResponse> = _location

     private fun getLocationDetails(latitude: Double, longitude: Double, units: String, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val locationDetails = _irepo.getLocationDetails(latitude , longitude, units, lang)
            _location.postValue(locationDetails)
        }
    }
}
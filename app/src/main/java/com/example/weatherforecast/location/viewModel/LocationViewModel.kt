package com.example.weatherforecast.location.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class LocationViewModel(private val _irepo: LocationRepository, latitude: Double, longitude: Double, units: String, lang: String) : ViewModel() {
    init {
        getLocationDetails(latitude , longitude, units, lang)
    }
    private var _location: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val location: StateFlow<ApiState> = _location

     private fun getLocationDetails(latitude: Double, longitude: Double, units: String, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _irepo.getLocationDetails(latitude , longitude, units, lang)
                .catch {e ->
                    _location.value = ApiState.Failure(e)
                }
                .collect{ data ->
                    _location.value = ApiState.Success(data)
                }
        }
    }
}
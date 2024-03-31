package com.example.weatherforecast.favouritePlaces.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.FavouriteLocation
import com.example.weatherforecast.model.LocationRepository
import com.example.weatherforecast.model.AlertState
import com.example.weatherforecast.model.PlacesState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavouritePlacesViewModel(private val _irepo: LocationRepository) : ViewModel() {
    init {
        getStoredPlaces()
    }
    private var _places: MutableStateFlow<PlacesState> = MutableStateFlow(PlacesState.Loading)
    val places: StateFlow<PlacesState> = _places

    fun deletePlace(favouriteLocation: FavouriteLocation){
        viewModelScope.launch(Dispatchers.IO) {
            _irepo.deletePlace(favouriteLocation)
            getStoredPlaces()
        }
    }
    fun insertPlace(favouriteLocation: FavouriteLocation){
        viewModelScope.launch(Dispatchers.IO) {
            _irepo.insertPlace(favouriteLocation)
        }
    }

    fun getStoredPlaces() {
        viewModelScope.launch(Dispatchers.IO) {
            _irepo.getStoredPlaces()
                .catch { e ->
                    _places.value = PlacesState.Failure(e)
                }
                .collect{ data ->
                    _places.value = PlacesState.Success(data)
                }
        }
    }
}
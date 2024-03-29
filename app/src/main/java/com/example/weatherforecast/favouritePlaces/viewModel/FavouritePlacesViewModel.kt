package com.example.weatherforecast.favouritePlaces.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.FavouriteLocation
import com.example.weatherforecast.model.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavouritePlacesViewModel(private val _irepo: LocationRepository) : ViewModel() {
    init {
        getStoredPlaces()
    }
    private var _places: MutableLiveData<List<FavouriteLocation>> = MutableLiveData<List<FavouriteLocation>>()
    val places: LiveData<List<FavouriteLocation>> = _places

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

    private fun getStoredPlaces() {
        viewModelScope.launch(Dispatchers.IO) {
            val placesList = _irepo.getStoredPlaces()
            _places.postValue(placesList)
        }
    }
}
package com.example.weatherforecast.model

sealed class PlacesState {
    class Success(val data: List<FavouriteLocation>?) : PlacesState()
    class Failure(val msg: Throwable) : PlacesState()
    object Loading : PlacesState()
}

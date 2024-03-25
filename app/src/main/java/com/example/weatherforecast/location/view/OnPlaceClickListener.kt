package com.example.weatherforecast.location.view

import com.example.weatherforecast.model.LatLng

interface OnPlaceClickListener {
    fun onFavouritePlaceClick(latLng: LatLng)
}


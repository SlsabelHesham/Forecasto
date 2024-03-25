package com.example.weatherforecast.dp

import com.example.weatherforecast.model.LatLng

interface LocationLocalDataSource {

    suspend fun getStoredPlaces(): List<LatLng>

    suspend fun insertPlace(latLng: LatLng)

    suspend fun deletePlace(latLng: LatLng)
}
package com.example.weatherforecast.model

interface LocationRepository {

    suspend fun getLocationDetails(latitude: Double , longitude: Double): WeatherApiResponse?

    suspend fun getStoredPlaces(): List<LatLng>

    suspend fun insertPlace(latLng: LatLng)

    suspend fun deletePlace(latLng: LatLng)
}
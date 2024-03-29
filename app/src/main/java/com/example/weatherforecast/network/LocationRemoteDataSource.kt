package com.example.weatherforecast.network

import com.example.weatherforecast.model.WeatherApiResponse

interface LocationRemoteDataSource {
    suspend fun getLocationDetailsOverNetwork(latitude: Double , longitude: Double, units: String, lang: String): WeatherApiResponse?
}


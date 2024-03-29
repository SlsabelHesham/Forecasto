package com.example.weatherforecast.network

import com.example.weatherforecast.model.WeatherApiResponse
import kotlinx.coroutines.flow.Flow

interface LocationRemoteDataSource {
    suspend fun getLocationDetailsOverNetwork(latitude: Double , longitude: Double, units: String, lang: String): Flow<WeatherApiResponse?>
}


package com.example.weatherforecast.network

import com.example.weatherforecast.model.City
import com.example.weatherforecast.model.Coord
import com.example.weatherforecast.model.WeatherApiResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocationRemoteDataSourceImplementation: LocationRemoteDataSource {
    private val weatherService : WeatherService by lazy {
        RetrofitHelper.retrofitInstance.create(WeatherService::class.java)
    }
    override suspend fun getLocationDetailsOverNetwork(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String
    ): Flow<WeatherApiResponse?> {
        val response =  weatherService.getWeatherForecast(latitude, longitude, "f11bd02e0587e48316013cf38a998f56", units, lang).body()
        return flow{
            response?.let { emit(it) } ?: emit(WeatherApiResponse(listOf(), City("", Coord(0.0 , 0.0))))
        }
    }
}
package com.example.weatherforecast.network

import com.example.weatherforecast.model.WeatherApiResponse

class LocationRemoteDataSourceImplementation: LocationRemoteDataSource {
    private val weatherService : WeatherService by lazy {
        RetrofitHelper.retrofitInstance.create(WeatherService::class.java)
    }
    override suspend fun getLocationDetailsOverNetwork(latitude: Double , longitude: Double): WeatherApiResponse? {
        val response = weatherService.getWeatherForecast(latitude , longitude , "f11bd02e0587e48316013cf38a998f56").body()
        return response
    }
}
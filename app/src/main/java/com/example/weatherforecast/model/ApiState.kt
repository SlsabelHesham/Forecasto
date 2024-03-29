package com.example.weatherforecast.model

sealed class ApiState {
    class Success(val data: WeatherApiResponse?) : ApiState()
    class Failure(val msg: Throwable) : ApiState()
    object Loading : ApiState()
}

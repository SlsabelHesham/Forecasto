package com.example.weatherforecast.model

sealed class AlertState {
    class Success(val data: List<Alert>?) : AlertState()
    class Failure(val msg: Throwable) : AlertState()
    object Loading : AlertState()
}

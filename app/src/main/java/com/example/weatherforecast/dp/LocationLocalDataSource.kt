package com.example.weatherforecast.dp

import com.example.weatherforecast.model.Alert
import com.example.weatherforecast.model.FavouriteLocation


interface LocationLocalDataSource {
    suspend fun getStoredPlaces(): List<FavouriteLocation>

    suspend fun insertPlace(favouriteLocation: FavouriteLocation)

    suspend fun deletePlace(favouriteLocation: FavouriteLocation)

    suspend fun getStoredAlerts(): List<Alert>

    suspend fun insertAlert(alert: Alert): Long

    suspend fun deleteAlert(alert: Alert)

    suspend fun getLastInsertedId(): Int?

    fun getAlertById(id: Int): Alert?
}
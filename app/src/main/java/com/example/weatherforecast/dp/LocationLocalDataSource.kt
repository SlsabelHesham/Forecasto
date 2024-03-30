package com.example.weatherforecast.dp

import com.example.weatherforecast.model.Alert
import com.example.weatherforecast.model.FavouriteLocation
import kotlinx.coroutines.flow.Flow


interface LocationLocalDataSource {
    suspend fun getStoredPlaces(): Flow<List<FavouriteLocation>>

    suspend fun insertPlace(favouriteLocation: FavouriteLocation)

    suspend fun deletePlace(favouriteLocation: FavouriteLocation)
    suspend fun getPlaceById(id: Int): FavouriteLocation?
    suspend fun getStoredAlerts(): Flow<List<Alert>>

    suspend fun insertAlert(alert: Alert): Long

    suspend fun deleteAlert(alert: Alert)
    suspend fun getAlertById(id: Int): Alert?
}
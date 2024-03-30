package com.example.weatherforecast.model

import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    suspend fun getLocationDetails(latitude: Double , longitude: Double, units: String, lang: String): Flow<WeatherApiResponse?>

    suspend fun getStoredPlaces(): Flow<List<FavouriteLocation>>

    suspend fun insertPlace(favouriteLocation: FavouriteLocation)

    suspend fun deletePlace(favouriteLocation: FavouriteLocation)

    suspend fun getStoredAlerts(): Flow<List<Alert>>
    suspend fun insertAlert(alert: Alert): Long
    suspend fun deleteAlert(alert: Alert)
    suspend fun getAlertById(id: Int): Alert?
}
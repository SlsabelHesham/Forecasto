package com.example.weatherforecast.model

interface LocationRepository {

    suspend fun getLocationDetails(latitude: Double , longitude: Double, units: String, lang: String): WeatherApiResponse?

    suspend fun getStoredPlaces(): List<FavouriteLocation>

    suspend fun insertPlace(favouriteLocation: FavouriteLocation)

    suspend fun deletePlace(favouriteLocation: FavouriteLocation)

    suspend fun getStoredAlerts(): List<Alert>

    suspend fun insertAlert(alert: Alert): Long

    suspend fun deleteAlert(alert: Alert)

    suspend fun getLastInsertedId(): Int?
    fun getAlertById(id: Int): Alert?
}
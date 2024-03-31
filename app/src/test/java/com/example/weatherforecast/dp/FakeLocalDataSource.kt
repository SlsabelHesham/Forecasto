package com.example.weatherforecast.dp

import com.example.weatherforecast.model.Alert
import com.example.weatherforecast.model.FavouriteLocation
import com.example.weatherforecast.model.WeatherApiResponse
import com.example.weatherforecast.network.LocationRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalDataSource(private var locations: MutableList<FavouriteLocation>? = mutableListOf(), private var alerts: MutableList<Alert>? = mutableListOf()): LocationLocalDataSource , LocationRemoteDataSource {

    override suspend fun getStoredPlaces(): Flow<List<FavouriteLocation>> {
        return flowOf(locations?.toList() ?: listOf())
    }

    override suspend fun insertPlace(favouriteLocation: FavouriteLocation) {
        locations?.add(favouriteLocation)
    }

    override suspend fun deletePlace(favouriteLocation: FavouriteLocation) {
        locations?.remove(favouriteLocation)
    }

    override suspend fun getPlaceById(id: Int): FavouriteLocation? {
        return locations?.find { it.id == id }
    }

    override suspend fun getStoredAlerts(): Flow<List<Alert>> {
        return flowOf(alerts?.toList() ?: listOf())
    }

    override suspend fun insertAlert(alert: Alert): Long {
        return if (alerts?.add(alert) == true) {
            alert.id.toLong()
        } else {
            -1L
        }
    }

    override suspend fun deleteAlert(alert: Alert) {
        alerts?.remove(alert)
    }

    override suspend fun getAlertById(id: Int): Alert? {
        return alerts?.find { it.id == id }
    }

    override suspend fun getLocationDetailsOverNetwork(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String
    ): Flow<WeatherApiResponse?> {
        TODO("Not yet implemented")
    }
}
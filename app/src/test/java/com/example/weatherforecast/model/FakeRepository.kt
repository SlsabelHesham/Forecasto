package com.example.weatherforecast.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRepository : LocationRepository {
    private val fakeLocations = mutableListOf<FavouriteLocation>()
    private val fakeAlerts = mutableListOf<Alert>()

    override suspend fun getLocationDetails(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String
    ): Flow<WeatherApiResponse?> {
        TODO("Not yet implemented")
    }

    override suspend fun getStoredPlaces(): Flow<List<FavouriteLocation>> {
        return flowOf(fakeLocations.toList())
        }

    override suspend fun insertPlace(favouriteLocation: FavouriteLocation) {
        fakeLocations.add(favouriteLocation)
    }

    override suspend fun deletePlace(favouriteLocation: FavouriteLocation) {
        fakeLocations.remove(favouriteLocation)
    }

    override suspend fun getPlaceById(id: Int): FavouriteLocation? {
        return fakeLocations.find { it.id == id }
    }

    override suspend fun getStoredAlerts(): Flow<List<Alert>> {
        return flowOf(fakeAlerts.toList())
    }

    override suspend fun insertAlert(alert: Alert): Long {
        return if (fakeAlerts.add(alert)) {
            alert.id.toLong()
        } else {
            -1L
        }
    }

    override suspend fun deleteAlert(alert: Alert) {
        fakeAlerts.remove(alert)
    }

    override suspend fun getAlertById(id: Int): Alert? {
        return fakeAlerts.find { it.id == id }
    }
}

package com.example.weatherforecast.model

import com.example.weatherforecast.dp.LocationLocalDataSource
import com.example.weatherforecast.network.LocationRemoteDataSource

class LocationRepositoryImplementation private constructor(
    private var locationRemoteDataSource: LocationRemoteDataSource,
    private var locationLocalDataSource: LocationLocalDataSource
): LocationRepository {
    companion object{
        private var instance: LocationRepositoryImplementation? = null
        fun getInstance(
            locationRemoteDataSource: LocationRemoteDataSource,
            locationLocalDataSource: LocationLocalDataSource
        ): LocationRepositoryImplementation {
            return instance ?: synchronized(this){
                val temp = LocationRepositoryImplementation(
                    locationRemoteDataSource, locationLocalDataSource)
                instance = temp
                temp
            }
        }
    }

    override suspend fun getLocationDetails(latitude: Double , longitude: Double, units: String, lang: String): WeatherApiResponse? {
        return locationRemoteDataSource.getLocationDetailsOverNetwork(latitude , longitude, units, lang)
    }

    override suspend fun getStoredPlaces(): List<FavouriteLocation> {
        return locationLocalDataSource.getStoredPlaces()
    }

    override suspend fun insertPlace(favouriteLocation: FavouriteLocation) {
        locationLocalDataSource.insertPlace(favouriteLocation)
    }

    override suspend fun deletePlace(favouriteLocation: FavouriteLocation) {
        locationLocalDataSource.deletePlace(favouriteLocation)
    }

    override suspend fun getStoredAlerts(): List<Alert> {
        return locationLocalDataSource.getStoredAlerts()
    }

    override suspend fun insertAlert(alert: Alert): Long {
        return locationLocalDataSource.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: Alert) {
        locationLocalDataSource.deleteAlert(alert)
    }

    override suspend fun getLastInsertedId(): Int? {
        return locationLocalDataSource.getLastInsertedId()
    }

    override fun getAlertById(id: Int): Alert? {
        return locationLocalDataSource.getAlertById(id)
    }
}
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

    override suspend fun getLocationDetails(latitude: Double , longitude: Double): WeatherApiResponse? {
        return locationRemoteDataSource.getLocationDetailsOverNetwork(latitude , longitude)
    }

    override suspend fun getStoredPlaces(): List<LatLng> {
        return locationLocalDataSource.getStoredPlaces()
    }

    override suspend fun insertPlace(latLng: LatLng) {
        locationLocalDataSource.insertPlace(latLng)
    }

    override suspend fun deletePlace(latLng: LatLng) {
        locationLocalDataSource.deletePlace(latLng)
    }
}
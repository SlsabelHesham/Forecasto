package com.example.weatherforecast.dp

import android.content.Context
import com.example.weatherforecast.model.LatLng

class LocationLocalDataSourceImplementation (context: Context) : LocationLocalDataSource {
    private val dao: LocationDAO by lazy {
        val db: LocationDatabase = LocationDatabase.getInstance(context)
        db.getPlaceDAO()
    }

    override suspend fun getStoredPlaces(): List<LatLng> {
        return dao.getAllPlaces()
    }

    override suspend fun insertPlace(latLng: LatLng) {
        dao.insertPlace(latLng)
    }

    override suspend fun deletePlace(latLng: LatLng) {
        dao.deletePlace(latLng)
    }
}
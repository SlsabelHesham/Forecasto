package com.example.weatherforecast.dp

import android.content.Context
import com.example.weatherforecast.model.Alert
import com.example.weatherforecast.model.FavouriteLocation
import kotlinx.coroutines.flow.Flow

class LocationLocalDataSourceImplementation (context: Context) : LocationLocalDataSource {
    private val dao: LocationDAO by lazy {
        val db: LocationDatabase = LocationDatabase.getInstance(context)
        db.getPlaceDAO()
    }

    override suspend fun getStoredPlaces(): Flow<List<FavouriteLocation>> {
        return dao.getAllPlaces()
    }

    override suspend fun insertPlace(favouriteLocation: FavouriteLocation) {
        dao.insertPlace(favouriteLocation)
    }

    override suspend fun deletePlace(favouriteLocation: FavouriteLocation) {
        dao.deletePlace(favouriteLocation)
    }

    override suspend fun getStoredAlerts(): Flow<List<Alert>> {
        return dao.getAllAlerts()
    }

    override suspend fun insertAlert(alert: Alert): Long {
        return dao.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: Alert) {
        dao.deleteAlert(alert)
    }

    override suspend fun getLastInsertedId(): Int? {
        return dao.getLastInsertedId()
    }

    override fun getAlertById(id: Int): Alert? {
        return dao.getAlertById(id)
    }


}
package com.example.weatherforecast.dp

import androidx.room.*
import com.example.weatherforecast.model.Alert
import com.example.weatherforecast.model.FavouriteLocation

@Dao
interface LocationDAO {
    @Query("SELECT * FROM places_table")
    fun getAllPlaces(): List<FavouriteLocation>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPlace(favouriteLocation: FavouriteLocation)

    @Delete
    fun deletePlace(favouriteLocation: FavouriteLocation)

    @Query("SELECT * FROM alerts_table")
    fun getAllAlerts(): List<Alert>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAlert(alert: Alert): Long

    @Delete
    fun deleteAlert(alert: Alert)

    @Query("SELECT id FROM alerts_table ORDER BY id DESC")
    fun getLastInsertedId(): Int?

    @Query("SELECT * FROM alerts_table WHERE id = :id")
    fun getAlertById(id: Int): Alert?

}
package com.example.weatherforecast.dp

import androidx.room.*
import com.example.weatherforecast.model.Alert
import com.example.weatherforecast.model.FavouriteLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDAO {
    @Query("SELECT * FROM places_table")
    fun getAllPlaces(): Flow<List<FavouriteLocation>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPlace(favouriteLocation: FavouriteLocation)

    @Delete
    fun deletePlace(favouriteLocation: FavouriteLocation)

    @Query("SELECT * FROM places_table WHERE id = :id")
    suspend fun getPlaceById(id: Int): FavouriteLocation?



    @Query("SELECT * FROM alerts_table")
    fun getAllAlerts(): Flow<List<Alert>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAlert(alert: Alert): Long

    @Delete
    fun deleteAlert(alert: Alert)

    @Query("SELECT * FROM alerts_table WHERE id = :id")
    fun getAlertById(id: Int): Alert?

}
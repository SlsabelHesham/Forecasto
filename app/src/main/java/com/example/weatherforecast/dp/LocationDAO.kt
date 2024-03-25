package com.example.weatherforecast.dp

import androidx.room.*
import com.example.weatherforecast.model.LatLng

@Dao
interface LocationDAO {
    @Query("SELECT * FROM places_table")
    fun getAllPlaces(): List<LatLng>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPlace(latLng: LatLng)

    @Delete
    fun deletePlace(latLng: LatLng)
}
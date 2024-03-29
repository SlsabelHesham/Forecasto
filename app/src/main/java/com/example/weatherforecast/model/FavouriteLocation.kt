package com.example.weatherforecast.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places_table")
data class FavouriteLocation(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val address: String
) {
    constructor(latitude: Double, longitude: Double, cityName: String, address: String) : this(0, latitude, longitude, cityName, address)
}

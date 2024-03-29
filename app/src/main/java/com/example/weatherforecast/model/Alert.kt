package com.example.weatherforecast.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts_table")
data class Alert(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val date: String,
    val time: String,
    val type: String
) {
    constructor(latitude: Double, longitude: Double, cityName: String, date: String , time: String , type: String) : this(0, latitude, longitude, cityName, date, time, type)
}

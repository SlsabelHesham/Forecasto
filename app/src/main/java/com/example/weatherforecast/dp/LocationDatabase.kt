package com.example.weatherforecast.dp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherforecast.model.LatLng

@Database(entities = [LatLng::class], version = 1)
abstract class LocationDatabase : RoomDatabase() {

    abstract fun getPlaceDAO(): LocationDAO

    companion object {
        @Volatile
        private var INSTANCE: LocationDatabase? = null

        fun getInstance(context: Context): LocationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, LocationDatabase::class.java, "places").build()
                INSTANCE = instance
                instance
            }
        }
    }
}
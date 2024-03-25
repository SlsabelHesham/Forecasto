package com.example.weatherforecast.model

import com.google.gson.annotations.SerializedName


data class WeatherApiResponse(
    val list: List<WeatherItem>,
    val city: City
)

data class WeatherItem(
    val main: Main,
    val weather: List<Weather>,
    val dt: Long,
    val clouds: Clouds,
    val wind: Wind,
    val dt_txt: String,
    val snow: Snow,
    val rain: Rain
)

data class Main(
    val temp: Double,
    val pressure: Int,
    val humidity: Int
)

data class Weather(
    val main: String,
    val description: String,
    val icon: String
)

data class Clouds(
    val all: Int
)

data class Wind(
    val speed: Double
)

data class Snow(
    @SerializedName("3h")
    val volumeInLast3Hours: Double
)
data class Rain(
    @SerializedName("3h")
    val volumeInLast3Hours: Double
)

data class City(
    val name: String,
    val coord: Coord
)
data class Coord(
    val lat: Double,
    val lon: Double
)
package com.example.weatherforecast.model

import com.example.weatherforecast.dp.FakeLocalDataSource
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

internal class LocationRepositoryTest {
    private val favouriteLocation1 = FavouriteLocation(1, 0.0, 0.0, "City1", "Address1")
    private val favouriteLocation2 = FavouriteLocation(2, 0.0, 0.0, "City2", "Address2")
    private val favouriteLocation3 = FavouriteLocation(3, 0.0, 0.0, "City3", "Address3")
    private val favouriteLocation4 = FavouriteLocation(4, 0.0, 0.0, "City4", "Address4")

    private val alert1 = Alert(10, 0.0, 0.0, "city1", "29/3/2023", "2:50 AM", "notification")
    private val alert2 = Alert(11, 0.0, 0.0, "city2", "30/3/2023", "2:50 AM", "alarm")

    private val remoteList = listOf(favouriteLocation1, favouriteLocation2)

    private val favouriteLocationList = listOf(favouriteLocation3, favouriteLocation4)
    private val alertsList = listOf(alert1, alert2)
    private lateinit var locationRemoteDataSource: FakeLocalDataSource

    private lateinit var locationLocalDataSource: FakeLocalDataSource

    private lateinit var locationRepository: LocationRepository

    @Before
    fun createRepository() {
        locationRemoteDataSource = FakeLocalDataSource(remoteList.toMutableList())
        locationLocalDataSource = FakeLocalDataSource(favouriteLocationList.toMutableList(), alertsList.toMutableList())
        locationRepository =
            LocationRepositoryImplementation(locationRemoteDataSource, locationLocalDataSource)
    }


    @Test
    fun testGetStoredPlaces() = runBlocking {
        val flow = locationRepository.getStoredPlaces()
        val retrievedList = mutableListOf<FavouriteLocation>()
        flow.collect { locations ->
            retrievedList.addAll(locations)
        }
        assertThat(favouriteLocationList, `is` (retrievedList))
    }


    @Test
    fun testInsertPlace() = runBlocking {
        locationRepository.insertPlace(favouriteLocation1)
        val getLocation = locationRepository.getPlaceById(favouriteLocation1.id)

        assertThat(getLocation, `is` (favouriteLocation1))
    }

    @Test
    fun testDeletePlace() = runBlocking {
        locationRepository.deletePlace(favouriteLocation1)
        val getLocation = locationRepository.getPlaceById(favouriteLocation1.id)

        assertThat(getLocation, `is` (nullValue()))
    }

    @Test
    fun testGetStoredAlerts() = runBlocking {
        val flow = locationRepository.getStoredAlerts()
        val retrievedList = mutableListOf<Alert>()
        flow.collect { alerts ->
            retrievedList.addAll(alerts)
        }
        assertThat(alertsList, `is` (retrievedList))
    }


    @Test
    fun testInsertAlert() = runBlocking {
        locationRepository.insertAlert(alert1)
        val getAlert = locationRepository.getAlertById(alert1.id)

        assertThat(getAlert, `is` (alert1))
    }

    @Test
    fun testDeleteAlert() = runBlocking {
        locationRepository.deleteAlert(alert1)
        val getAlert = locationRepository.getAlertById(alert1.id)

        assertThat(getAlert, `is` (nullValue()))
    }


}
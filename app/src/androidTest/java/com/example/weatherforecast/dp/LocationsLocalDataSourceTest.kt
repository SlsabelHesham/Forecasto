package com.example.weatherforecast.dp

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecast.model.Alert
import com.example.weatherforecast.model.FavouriteLocation
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocationsLocalDataSourceTest {

    private lateinit var localDataSource: LocationLocalDataSourceImplementation
    private lateinit var database: LocationDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder (
            ApplicationProvider.getApplicationContext(), LocationDatabase::class.java).build()
        localDataSource = LocationLocalDataSourceImplementation(ApplicationProvider.getApplicationContext())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testGetAllAlerts() = runBlocking {
        // Given: insert some favourite locations into the database
        val favouriteLocations = listOf(
            FavouriteLocation(1, 0.0, 0.0, "City1", "Address1"),
            FavouriteLocation(2, 0.0, 0.0, "City2", "Address2"),
            FavouriteLocation(3, 0.0, 0.0, "City3", "Address3")
        )
        favouriteLocations.forEach{
            localDataSource.insertPlace(it)
        }

        // When: get all places from the database
        val placesFlow = localDataSource.getStoredPlaces()

        // Then: assert that the retrieved data matches the inserted data
        val allPlacesList = placesFlow.firstOrNull()
        assertThat(placesFlow, notNullValue())
        assertThat(favouriteLocations, `is` (allPlacesList))
    }

    @Test
    fun insertAlertAndGetById() = runBlocking {
        //Given: insert an alert
        val alert = Alert(10, 0.0, 0.0, "city", "29/3/2023", "2:50 AM", "notification")
        localDataSource.insertAlert(alert)

        //When: get the inserted alert from the database
        val loaded = localDataSource.getAlertById(alert.id)

        //Then: assert that the loaded data contains the expected values
        assertThat(loaded as Alert, notNullValue())
        assertThat(loaded.id, `is`(alert.id))
        assertThat(loaded.cityName, `is`(alert.cityName))
        assertThat(loaded.date, `is`(alert.date))
    }

    @Test
    fun deleteAlertAndGetById() = runBlocking {
        // Given: insert an alert
        val alert = Alert(10, 0.0, 0.0, "city", "29/3/2023", "2:50 AM", "notification")
        localDataSource.insertAlert(alert)

        // When: delete the inserted alert from the database
        localDataSource.deleteAlert(alert)

        // Then: assert that the loaded data is null
        val loaded = localDataSource.getAlertById(alert.id)
        assertThat(loaded, nullValue())
    }

    @Test
    fun insertPlaceAndGetById() = runBlocking {
        //Given: insert a favourite location
        val favouriteLocation = FavouriteLocation(10,0.0, 0.0, "City", "Address")
        localDataSource.insertPlace(favouriteLocation)

        //When: get the inserted favourite location from the database
        val loaded = localDataSource.getPlaceById(favouriteLocation.id)

        //Then: assert that the loaded data contains the expected values
        assertThat(loaded as FavouriteLocation, notNullValue())
        assertThat(loaded.id, `is`(favouriteLocation.id))
        assertThat(loaded.cityName, `is`(favouriteLocation.cityName))
        assertThat(loaded.address, `is`(favouriteLocation.address))
    }

    @Test
    fun deletePlaceAndGetById() = runBlocking {
        // Given: insert a favourite location
        val favouriteLocation = FavouriteLocation(10, 0.0, 0.0, "City", "Address")
        localDataSource.insertPlace(favouriteLocation)

        // When: delete the inserted favourite location from the database
        localDataSource.deletePlace(favouriteLocation)

        // Then: assert that the loaded data is null
        val loaded = localDataSource.getPlaceById(favouriteLocation.id)
        assertThat(loaded, nullValue())
    }

    @Test
    fun testGetAllPlaces() = runBlocking {
        // Given: insert some favourite locations into the database
        val favouriteLocations = listOf(
            FavouriteLocation(1, 0.0, 0.0, "City1", "Address1"),
            FavouriteLocation(2, 0.0, 0.0, "City2", "Address2"),
            FavouriteLocation(3, 0.0, 0.0, "City3", "Address3")
        )
        favouriteLocations.forEach{
            localDataSource.insertPlace(it)
        }
        // When: get all places from the database
        val placesFlow = localDataSource.getStoredPlaces()

        // Then: assert that the retrieved data matches the inserted data
        val allPlacesList = placesFlow.firstOrNull()
        assertThat(placesFlow, notNullValue())
        assertThat(favouriteLocations, `is` (allPlacesList))
    }

}
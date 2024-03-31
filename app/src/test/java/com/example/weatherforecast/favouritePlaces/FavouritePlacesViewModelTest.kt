package com.example.weatherforecast.favouritePlaces

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecast.favouritePlaces.viewModel.FavouritePlacesViewModel
import com.example.weatherforecast.model.Alert
import com.example.weatherforecast.model.AlertState
import com.example.weatherforecast.model.FakeRepository
import com.example.weatherforecast.model.FavouriteLocation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@Suppress("DEPRECATION")
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class FavouritePlacesViewModelTest {

    @get:Rule
    val myRule = InstantTaskExecutorRule()

    private lateinit var viewModel: FavouritePlacesViewModel
    private lateinit var repo: FakeRepository

    @Before
    fun setUp() {
        //Given: Create object from FakeRepository
        repo = FakeRepository()
        viewModel = FavouritePlacesViewModel(repo)
    }

    @Test
    fun `test getStoredPlaces success`() = runBlockingTest {

        val fakeAlerts = listOf(
            Alert(1, 0.0, 0.0, "city1", "29/3/2023", "2:50 AM", "notification"),
            Alert(2, 0.0, 0.0, "city2", "30/3/2023", "3:00 AM", "alarm")
        )

        fakeAlerts.forEach {
            repo.insertAlert(it)
        }
        viewModel.getStoredPlaces()

        assertThat(AlertState.Success(fakeAlerts).data, `is` (fakeAlerts))
    }

    @Test
    fun `test insertPlace success`() = runBlockingTest {
        val favouriteLocation1 = FavouriteLocation(1, 0.0, 0.0, "City1", "Address1")

        viewModel.insertPlace(favouriteLocation1)
        this.advanceUntilIdle()
        assertThat(repo.getPlaceById(favouriteLocation1.id), `is`(favouriteLocation1))
    }

    @Test
    fun `test deletePlace success`() = runBlockingTest {
        val favouriteLocation1 = FavouriteLocation(1, 0.0, 0.0, "City1", "Address1")

        repo.insertPlace(favouriteLocation1)

        viewModel.deletePlace(favouriteLocation1)
        this.advanceUntilIdle()
        assertThat(repo.getPlaceById(favouriteLocation1.id), `is`(nullValue()))
    }
}



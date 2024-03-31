@file:Suppress("DEPRECATION")

package com.example.weatherforecast.alerts

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.distinctUntilChanged
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecast.alert.viewModel.AlertsViewModel
import com.example.weatherforecast.model.Alert
import com.example.weatherforecast.model.AlertState
import com.example.weatherforecast.model.FakeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class AlertViewModelTest {

    @get:Rule
    val myRule = InstantTaskExecutorRule()

    private lateinit var viewModel: AlertsViewModel
    private lateinit var repo: FakeRepository

    @Before
    fun setUp() {
        //Given: Create object from FakeRepository
        repo = FakeRepository()
        viewModel = AlertsViewModel(repo)
    }

    @Test
    fun `test getStoredAlerts success`() = runBlockingTest {
        val fakeAlerts = listOf(
            Alert(1, 0.0, 0.0, "city1", "29/3/2023", "2:50 AM", "notification"),
            Alert(2, 0.0, 0.0, "city2", "30/3/2023", "3:00 AM", "alarm")
        )

        fakeAlerts.forEach {
            repo.insertAlert(it)
        }
        viewModel.getStoredAlerts()

        assertThat(AlertState.Success(fakeAlerts).data, `is` (fakeAlerts))
    }

    @Test
    fun `test insertAlerts success`() = runBlockingTest {
        val alert = Alert(1, 0.0, 0.0, "city1", "29/3/2023", "2:50 AM", "notification")

        viewModel.insertAlert(alert)
        assertThat(viewModel.lastInsertedId.distinctUntilChanged().value?.toInt() , `is` (alert.id))
    }

    @Test
    fun `test deleteAlert success`() = runBlockingTest {
        val a1 = Alert(1, 0.0, 0.0, "city1", "29/3/2023", "2:50 AM", "notification")

        viewModel.insertAlert(a1)
        this.advanceUntilIdle()
        assertThat(repo.getAlertById(a1.id), `is`(a1))

        viewModel.deleteAlert(a1)
        this.advanceUntilIdle()
        assertThat(repo.getAlertById(a1.id), `is`(nullValue()))
    }
}



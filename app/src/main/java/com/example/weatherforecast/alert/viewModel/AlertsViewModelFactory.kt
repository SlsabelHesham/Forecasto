package com.example.weatherforecast.alert.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.model.LocationRepository


@Suppress("UNCHECKED_CAST")
class AlertsViewModelFactory (private val _repo: LocationRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlertsViewModel::class.java)) {
            AlertsViewModel(_repo) as T
        } else {
            throw IllegalArgumentException("ViewModel Class not found")
        }
    }
}
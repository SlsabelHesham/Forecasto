package com.example.weatherforecast.alert.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.Alert
import com.example.weatherforecast.model.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlertsViewModel(private val _irepo: LocationRepository) : ViewModel() {
    init {
        getStoredAlerts()
    }
    private var _alerts: MutableLiveData<List<Alert>> = MutableLiveData<List<Alert>>()
    val alerts: LiveData<List<Alert>> = _alerts

    private val _lastInsertedId = MutableLiveData<Long>()
    val lastInsertedId: LiveData<Long> = _lastInsertedId

    fun deleteAlert(alert: Alert){
        viewModelScope.launch(Dispatchers.IO) {
            _irepo.deleteAlert(alert)
            getStoredAlerts()
        }
    }
    fun insertAlert(alert: Alert){
        viewModelScope.launch(Dispatchers.IO) {
            val id = _irepo.insertAlert(alert)
            _lastInsertedId.postValue(id)
        }
    }

    fun getStoredAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            val alertsList = _irepo.getStoredAlerts()
            _alerts.postValue(alertsList)
        }
    }
}
package com.example.android.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.data.model.DeviceListItemData
import com.example.android.data.network.DeviceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DeviceListUiState {
    object Loading : DeviceListUiState()
    data class Success(val devices: List<DeviceListItemData>) : DeviceListUiState()
    data class Error(val message: String) : DeviceListUiState()
}

class DeviceListViewModel : ViewModel() {

    private val repository = DeviceRepository()

    private val _uiState = MutableStateFlow<DeviceListUiState>(DeviceListUiState.Loading)
    val uiState: StateFlow<DeviceListUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = DeviceListUiState.Loading
            repository.getDevices()
                .onSuccess { _uiState.value = DeviceListUiState.Success(it.devices) }
                .onFailure { _uiState.value = DeviceListUiState.Error(it.message ?: "오류 발생") }
        }
    }
}

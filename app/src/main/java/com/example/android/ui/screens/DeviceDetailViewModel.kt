package com.example.android.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.android.data.model.DeviceDetailData
import com.example.android.data.network.DeviceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DeviceDetailUiState {
    object Loading : DeviceDetailUiState()
    data class Success(val data: DeviceDetailData) : DeviceDetailUiState()
    data class Error(val message: String) : DeviceDetailUiState()
}

class DeviceDetailViewModel(private val deviceId: Long) : ViewModel() {

    private val repository = DeviceRepository()

    private val _uiState = MutableStateFlow<DeviceDetailUiState>(DeviceDetailUiState.Loading)
    val uiState: StateFlow<DeviceDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = DeviceDetailUiState.Loading
            repository.getDeviceDetail(deviceId)
                .onSuccess { _uiState.value = DeviceDetailUiState.Success(it) }
                .onFailure { _uiState.value = DeviceDetailUiState.Error(it.message ?: "오류 발생") }
        }
    }

    companion object {
        fun factory(deviceId: Long): ViewModelProvider.Factory = viewModelFactory {
            initializer { DeviceDetailViewModel(deviceId) }
        }
    }
}

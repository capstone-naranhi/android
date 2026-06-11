package com.example.android.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.data.model.RegisterDeviceData
import com.example.android.data.model.RegisterDeviceRequest
import com.example.android.data.network.DeviceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ─── UI 상태 ──────────────────────────────────────────────────────────────────

sealed class DeviceRegisterUiState {
    object Idle : DeviceRegisterUiState()
    object Loading : DeviceRegisterUiState()
    data class Success(val device: RegisterDeviceData) : DeviceRegisterUiState()
    data class Error(val message: String) : DeviceRegisterUiState()
}

// ─── 폼 입력 상태 ──────────────────────────────────────────────────────────────

data class RegisterFormState(
    val deviceSerial: String   = "",
    val deviceName: String     = "",
    val locationName: String   = "",
    val serialError: String?   = null,
    val nameError: String?     = null,
    val locationError: String? = null
) {
    val isValid: Boolean
        get() = deviceSerial.isNotBlank() &&
                deviceName.isNotBlank() && deviceName.length <= 20 &&
                locationName.isNotBlank() && locationName.length <= 100
}

// ─── ViewModel ────────────────────────────────────────────────────────────────

class DeviceRegisterViewModel : ViewModel() {

    private val repository = DeviceRepository()

    private val _uiState = MutableStateFlow<DeviceRegisterUiState>(DeviceRegisterUiState.Idle)
    val uiState: StateFlow<DeviceRegisterUiState> = _uiState.asStateFlow()

    private val _form = MutableStateFlow(RegisterFormState())
    val form: StateFlow<RegisterFormState> = _form.asStateFlow()

    // ─── 입력 핸들러 ──────────────────────────────────────────────────────────

    fun onSerialChange(value: String) {
        _form.value = _form.value.copy(
            deviceSerial = value.trim().uppercase(),
            serialError  = null
        )
    }

    fun onNameChange(value: String) {
        _form.value = _form.value.copy(
            deviceName = value,
            nameError  = if (value.length > 20) "20자 이내로 입력해주세요" else null
        )
    }

    fun onLocationChange(value: String) {
        _form.value = _form.value.copy(
            locationName  = value,
            locationError = if (value.length > 100) "100자 이내로 입력해주세요" else null
        )
    }

    // ─── 등록 요청 ────────────────────────────────────────────────────────────

    fun register() {
        val f = _form.value

        // 클라이언트 유효성 검사
        val serialError   = if (f.deviceSerial.isBlank()) "시리얼 번호를 입력해주세요" else null
        val nameError     = when {
            f.deviceName.isBlank()   -> "장치 이름을 입력해주세요"
            f.deviceName.length > 20 -> "20자 이내로 입력해주세요"
            else                     -> null
        }
        val locationError = when {
            f.locationName.isBlank()    -> "설치 위치를 입력해주세요"
            f.locationName.length > 100 -> "100자 이내로 입력해주세요"
            else                        -> null
        }

        if (serialError != null || nameError != null || locationError != null) {
            _form.value = f.copy(
                serialError   = serialError,
                nameError     = nameError,
                locationError = locationError
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = DeviceRegisterUiState.Loading
            repository.registerDevice(
                RegisterDeviceRequest(
                    deviceSerial = f.deviceSerial,
                    deviceName   = f.deviceName,
                    locationName = f.locationName
                )
            ).onSuccess { device ->
                _uiState.value = DeviceRegisterUiState.Success(device)
            }.onFailure { e ->
                _uiState.value = DeviceRegisterUiState.Error(
                    e.message ?: "장치 등록에 실패했습니다"
                )
            }
        }
    }

    fun resetError() {
        if (_uiState.value is DeviceRegisterUiState.Error) {
            _uiState.value = DeviceRegisterUiState.Idle
        }
    }
}

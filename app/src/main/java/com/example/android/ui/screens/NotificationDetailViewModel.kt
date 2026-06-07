package com.example.android.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.android.data.model.NotificationDetailData
import com.example.android.data.network.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class NotificationDetailUiState {
    object Loading : NotificationDetailUiState()
    data class Success(val data: NotificationDetailData) : NotificationDetailUiState()
    data class Error(val message: String) : NotificationDetailUiState()
}

class NotificationDetailViewModel(
    private val notificationId: String
) : ViewModel() {

    private val repository = NotificationRepository()

    private val _uiState = MutableStateFlow<NotificationDetailUiState>(NotificationDetailUiState.Loading)
    val uiState: StateFlow<NotificationDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    private fun loadDetail() {
        viewModelScope.launch {
            // 상세 조회와 읽음 처리 동시 실행
            launch { repository.readNotification(notificationId) }
            repository.getNotificationDetail(notificationId)
                .onSuccess { _uiState.value = NotificationDetailUiState.Success(it) }
                .onFailure { _uiState.value = NotificationDetailUiState.Error(it.message ?: "알림을 불러올 수 없습니다.") }
        }
    }

    companion object {
        fun factory(notificationId: String) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                NotificationDetailViewModel(notificationId) as T
        }
    }
}

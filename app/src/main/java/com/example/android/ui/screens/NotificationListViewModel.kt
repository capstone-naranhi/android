package com.example.android.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.data.model.NotificationItem
import com.example.android.data.model.toNotificationItem
import com.example.android.data.network.NotificationRepository
import com.example.android.ui.components.NotificationFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NotificationListState(
    val items: List<NotificationItem> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val hasNext: Boolean = false,
    val nextCursorId: Long? = null,
    val unreadCount: Int = 0,
    val selectedFilter: NotificationFilter = NotificationFilter.ALL
)

class NotificationListViewModel : ViewModel() {

    private val repository = NotificationRepository()

    private val _state = MutableStateFlow(NotificationListState(isLoading = true))
    val state: StateFlow<NotificationListState> = _state.asStateFlow()

    init {
        loadFromStart()
        loadUnreadCount()
    }

    fun setFilter(filter: NotificationFilter) {
        if (_state.value.selectedFilter == filter) return
        _state.value = NotificationListState(selectedFilter = filter, isLoading = true)
        loadFromStart()
    }

    fun loadMore() {
        val current = _state.value
        if (!current.hasNext || current.isLoadingMore || current.isLoading) return
        _state.value = current.copy(isLoadingMore = true)
        viewModelScope.launch {
            repository.getNotificationList(
                type     = current.selectedFilter.toApiType(),
                cursorId = current.nextCursorId
            ).onSuccess { data ->
                val newItems = data.notifications.map { it.toNotificationItem() }
                _state.value = current.copy(
                    items        = current.items + newItems,
                    isLoadingMore = false,
                    hasNext      = data.hasNext,
                    nextCursorId = data.nextCursorId
                )
            }.onFailure {
                _state.value = current.copy(isLoadingMore = false)
            }
        }
    }

    fun refresh() {
        _state.value = _state.value.copy(isLoading = true, error = null, items = emptyList())
        loadFromStart()
        loadUnreadCount()
    }

    private fun loadFromStart() {
        val filter = _state.value.selectedFilter
        viewModelScope.launch {
            repository.getNotificationList(type = filter.toApiType(), cursorId = null)
                .onSuccess { data ->
                    val items = data.notifications.map { it.toNotificationItem() }
                    _state.value = _state.value.copy(
                        items        = items,
                        isLoading    = false,
                        error        = null,
                        hasNext      = data.hasNext,
                        nextCursorId = data.nextCursorId
                    )
                }
                .onFailure { t ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error     = t.message ?: "알림을 불러올 수 없습니다."
                    )
                }
        }
    }

    private fun loadUnreadCount() {
        viewModelScope.launch {
            repository.getUnreadCount()
                .onSuccess { count ->
                    _state.value = _state.value.copy(unreadCount = count)
                }
        }
    }

    private fun NotificationFilter.toApiType(): String? = when (this) {
        NotificationFilter.ALL     -> null
        NotificationFilter.SAFETY  -> "SAFETY"
        NotificationFilter.DEVICE  -> "DEVICE"
        NotificationFilter.GENERAL -> "GENERAL"
    }
}

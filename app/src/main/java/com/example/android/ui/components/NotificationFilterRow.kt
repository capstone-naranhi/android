package com.example.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android.ui.theme.NanumSquareRound
import com.example.android.ui.theme.NeutralSubText
import com.example.android.ui.theme.NeutralSurface
import com.example.android.ui.theme.NeutralText

enum class NotificationFilter { ALL, SAFETY, DEVICE, GENERAL }

@Composable
fun NotificationFilterRow(
    selectedFilter: NotificationFilter,
    onFilterSelected: (NotificationFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = listOf(
        NotificationFilter.ALL to "전체",
        NotificationFilter.SAFETY to "안전",
        NotificationFilter.DEVICE to "장치",
        NotificationFilter.GENERAL to "일반",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        filters.forEach { (filter, label) ->
            FilterChip(
                label = label,
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) }
            )
        }
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(if (selected) Color.White else NeutralSurface)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            fontFamily = NanumSquareRound,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp,
            color = if (selected) NeutralText else NeutralSubText
        )
    }
}

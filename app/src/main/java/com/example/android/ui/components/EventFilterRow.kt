package com.example.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.android.R
import com.example.android.ui.theme.Dimens

enum class EventFilter { ALL, DANGER, WARNING, INFO }

@Composable
fun EventFilterRow(
    selectedFilter: EventFilter,
    onFilterSelected: (EventFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = listOf(
        EventFilter.ALL to stringResource(R.string.alert_filter_all),
        EventFilter.DANGER to stringResource(R.string.alert_filter_danger),
        EventFilter.WARNING to stringResource(R.string.alert_filter_warning),
        EventFilter.INFO to stringResource(R.string.alert_filter_info),
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spaceXl)
            .padding(top = Dimens.spaceXl, bottom = Dimens.spaceM),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceS)
    ) {
        filters.forEach { (filter, label) ->
            EventFilterChip(
                label = label,
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) }
            )
        }
    }
}

@Composable
private fun EventFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    Box(
        modifier = modifier
            .background(
                color = if (selected) primaryColor else primaryColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(999.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) Color.White else primaryColor
        )
    }
}

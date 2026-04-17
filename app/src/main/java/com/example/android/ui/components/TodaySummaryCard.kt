package com.example.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.android.ui.theme.BrandPrimary
import com.example.android.ui.theme.Dimens
import com.example.android.ui.theme.NeutralSubText
import com.example.android.ui.theme.NeutralSurface

/**
 * 오늘의 요약 카드 안에서 사용하는 항목 1개
 *
 * @param label      항목명 (예: "위험 감지", "울음")
 * @param valueText  수치 (예: "2회", "3회")
 * @param valueColor 수치 색상 (기본값: [BrandPrimary])
 */
data class TodaySummaryMetric(
    val label: String,
    val valueText: String,
    val valueColor: Color = BrandPrimary
)

/**
 * 홈 화면 "오늘의 요약" 카드
 *
 * 바깥 SectionHeader와 함께 사용하는 구조를 전제로 한다.
 *
 * @param metrics 표시할 요약 항목 리스트
 */
@Composable
fun TodaySummaryCard(
    metrics: List<TodaySummaryMetric>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.radiusCard),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceXl)) {
            metrics.forEachIndexed { index, metric ->
                TodaySummaryMetricRow(metric = metric)
                if (index != metrics.lastIndex) {
                    Spacer(modifier = Modifier.height(Dimens.spaceM))
                }
            }
        }
    }
}

@Composable
private fun TodaySummaryMetricRow(metric: TodaySummaryMetric) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = NeutralSurface, shape = RoundedCornerShape(Dimens.radiusS))
            .padding(horizontal = Dimens.spaceL, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = metric.label,
            style = MaterialTheme.typography.bodyLarge,
            color = NeutralSubText
        )

        Spacer(modifier = Modifier.width(Dimens.spaceM))

        Text(
            text = metric.valueText,
            style = MaterialTheme.typography.titleMedium,
            color = metric.valueColor
        )
    }
}

package com.example.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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

/**
 * 오늘의 요약 카드 안에서 사용하는 항목 1개
 *
 * 예:
 * - 위험 감지 / 2회
 * - 울음 / 3회
 * - 칭얼거림 / 7회
 * - 비명 / 0회
 */
data class TodaySummaryMetric(
    val label: String,
    val valueText: String,
    val valueColor: Color = Color(0xFF264A7C)
)

/**
 * 홈 화면에서 "오늘의 요약"을 보여주는 카드
 *
 * 현재 버전은 카드 내부 제목/아이콘을 없애고,
 * 바깥 SectionHeader와 함께 사용하는 구조를 전제로 한다.
 *
 * 즉:
 * - 바깥: SectionHeader("오늘의 요약", statusText = "분석중")
 * - 안쪽: 요약 항목들만 표시
 *
 * @param metrics 표시할 요약 항목 리스트
 * @param modifier 외부에서 여백/크기 조절용 Modifier
 */
@Composable
fun TodaySummaryCard(
    metrics: List<TodaySummaryMetric>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(20.dp)
        ) {
            /**
             * 요약 항목 리스트
             *
             * 지금은 세로 리스트 구조.
             * 홈 화면에서 빠르게 훑어보기에 적합하다.
             */
            metrics.forEachIndexed { index, metric ->
                TodaySummaryMetricRow(metric = metric)

                if (index != metrics.lastIndex) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

/**
 * 오늘의 요약 카드 안에서 사용하는 한 줄 항목 UI
 *
 * 왼쪽: 항목명
 * 오른쪽: 수치/값
 */
@Composable
private fun TodaySummaryMetricRow(
    metric: TodaySummaryMetric
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF7F9FC),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = metric.label,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF5B6B84)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = metric.valueText,
            style = MaterialTheme.typography.titleMedium,
            color = metric.valueColor
        )
    }
}
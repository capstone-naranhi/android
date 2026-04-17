package com.example.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.android.ui.theme.Dimens
import com.example.android.ui.theme.NeutralText
import com.example.android.ui.theme.SuccessContent

/**
 * 화면 섹션 제목 컴포넌트
 *
 * 지원 형태:
 * - 액션형:  최근 이벤트       전체보기 >
 * - 상태형:  오늘의 요약       [아이콘] 분석중
 */
@Composable
fun SectionHeader(
    title: String,
    actionText: String? = null,
    statusText: String? = null,
    statusColor: Color = SuccessContent,   // 기본값을 테마 토큰으로
    showArrow: Boolean = true,
    onActionClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // action / label 색상을 MaterialTheme.colorScheme.primary 에서 가져옴
    val primaryColor = MaterialTheme.colorScheme.primary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = NeutralText
        )

        when {
            statusText != null -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Analytics,
                        contentDescription = "상태 표시 아이콘",
                        tint = statusColor,
                        modifier = Modifier.size(Dimens.iconS)
                    )
                    Spacer(modifier = Modifier.size(Dimens.spaceXs))
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelLarge,
                        color = statusColor
                    )
                }
            }

            actionText != null -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onActionClick() }
                ) {
                    Text(
                        text = actionText,
                        style = MaterialTheme.typography.labelLarge,
                        color = primaryColor
                    )
                    if (showArrow) {
                        Spacer(modifier = Modifier.size(Dimens.spaceXs))
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = "섹션 이동",
                            tint = primaryColor,
                            modifier = Modifier.size(Dimens.iconS)
                        )
                    }
                }
            }
        }
    }
}
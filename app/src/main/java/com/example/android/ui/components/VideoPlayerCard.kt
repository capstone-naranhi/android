package com.example.android.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.android.ui.theme.DangerContent
import com.example.android.ui.theme.Dimens
import com.example.android.ui.theme.VideoBackground
import kotlinx.coroutines.delay
import org.webrtc.EglBase
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack

/**
 * 영상 스트리밍 카드.
 *
 * @param videoTrack    WebRTC 원격 VideoTrack (null이면 플레이스홀더 표시)
 * @param eglBaseContext WebRtcClient.eglBase.eglBaseContext — SurfaceViewRenderer 초기화에 사용
 */
@Composable
fun VideoPlayerCard(
    videoTrack: VideoTrack? = null,
    eglBaseContext: EglBase.Context? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "live_blink")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = 0.25f,
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMillis = 700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha"
    )

    var elapsedSeconds by remember { mutableIntStateOf(0) }
    LaunchedEffect(videoTrack) {
        elapsedSeconds = 0
        if (videoTrack != null) {
            while (true) {
                delay(1000)
                elapsedSeconds++
            }
        }
    }
    val timerText = remember(elapsedSeconds) {
        val h = elapsedSeconds / 3600
        val m = (elapsedSeconds % 3600) / 60
        val s = elapsedSeconds % 60
        "%02d:%02d:%02d".format(h, m, s)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(color = VideoBackground, shape = RoundedCornerShape(Dimens.radiusCard))
            .clip(RoundedCornerShape(Dimens.radiusCard))
    ) {
        // ── 영상 렌더링 또는 플레이스홀더 ──────────────────────────────────────
        if (videoTrack != null && eglBaseContext != null) {
            WebRtcVideoSurface(
                videoTrack      = videoTrack,
                eglBaseContext  = eglBaseContext,
                modifier        = Modifier.fillMaxSize()
            )
        } else {
            PlaceholderContent(modifier = Modifier.align(Alignment.Center))
        }

        // ── LIVE 배지 ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(Dimens.spaceM)
                .background(color = DangerContent, shape = RoundedCornerShape(999.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .alpha(dotAlpha)
                    .background(color = Color.White, shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text       = "LIVE",
                style      = MaterialTheme.typography.labelLarge,
                color      = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 12.sp
            )
        }

        // ── 타이머 (스트리밍 중일 때만) ────────────────────────────────────────
        if (videoTrack != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(Dimens.spaceM)
                    .background(
                        color = Color.Black.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text       = timerText,
                    style      = MaterialTheme.typography.labelLarge,
                    color      = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 13.sp
                )
            }
        }

        // ── 전체화면 버튼 ─────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(Dimens.spaceM)
                .background(
                    color = Color.Black.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { /* 전체화면 전환 */ }
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Outlined.Fullscreen,
                contentDescription = "전체화면",
                tint               = Color.White,
                modifier           = Modifier.size(22.dp)
            )
        }
    }
}

// ─── WebRTC SurfaceViewRenderer ───────────────────────────────────────────────

@Composable
private fun WebRtcVideoSurface(
    videoTrack: VideoTrack,
    eglBaseContext: EglBase.Context,
    modifier: Modifier = Modifier
) {
    var renderer by remember { mutableStateOf<SurfaceViewRenderer?>(null) }

    AndroidView(
        factory = { ctx ->
            SurfaceViewRenderer(ctx).apply {
                init(eglBaseContext, null)
                setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
                setMirror(false)
            }.also { renderer = it }
        },
        modifier = modifier
    )

    // videoTrack이 바뀌면 sink 재연결
    DisposableEffect(videoTrack) {
        renderer?.let { videoTrack.addSink(it) }
        onDispose {
            renderer?.let { videoTrack.removeSink(it) }
        }
    }

    // 화면에서 사라질 때 renderer 해제
    DisposableEffect(Unit) {
        onDispose {
            renderer?.release()
            renderer = null
        }
    }
}

// ─── 플레이스홀더 ─────────────────────────────────────────────────────────────

@Composable
private fun PlaceholderContent(modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Column(
        modifier              = modifier,
        horizontalAlignment   = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    color = Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(Dimens.radiusCard)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Outlined.Fullscreen,
                contentDescription = null,
                tint               = Color.White.copy(alpha = 0.4f),
                modifier           = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(Dimens.spaceS))
        Text(
            text  = "연결 중...",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.5f)
        )
    }
}

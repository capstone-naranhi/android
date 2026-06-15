package com.example.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.android.data.model.RegisterDeviceData
import com.example.android.ui.theme.AppBackground
import com.example.android.ui.theme.BrandPrimary
import com.example.android.ui.theme.DangerContent
import com.example.android.ui.theme.InfoContent
import com.example.android.ui.theme.NanumSquareRound
import com.example.android.ui.theme.NeutralSubText
import com.example.android.ui.theme.NeutralText
import com.example.android.ui.theme.SafeCardAccent

// ─── 화면 ─────────────────────────────────────────────────────────────────────

@Composable
fun DeviceRegisterScreen(
    onBack: () -> Unit = {},
    onRegistered: (Long) -> Unit = {},
    viewModel: DeviceRegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val form    by viewModel.form.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .statusBarsPadding()
    ) {
        // 상단 바
        RegisterTopBar(onBack = onBack)
        HorizontalDivider(color = Color(0xFFF0F2F5))

        when (val state = uiState) {
            is DeviceRegisterUiState.Success -> {
                SuccessContent(
                    device   = state.device,
                    onGoToDevice = { onRegistered(state.device.deviceId) },
                    onBack   = onBack
                )
            }

            else -> {
                FormContent(
                    form       = form,
                    isLoading  = state is DeviceRegisterUiState.Loading,
                    apiError   = (state as? DeviceRegisterUiState.Error)?.message,
                    onSerialChange   = viewModel::onSerialChange,
                    onNameChange     = viewModel::onNameChange,
                    onLocationChange = viewModel::onLocationChange,
                    onRegister       = viewModel::register,
                    onDismissError   = viewModel::resetError
                )
            }
        }
    }
}

// ─── 상단 바 ──────────────────────────────────────────────────────────────────

@Composable
private fun RegisterTopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable { onBack() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                imageVector        = Icons.AutoMirrored.Outlined.ArrowBackIos,
                contentDescription = "뒤로",
                tint               = InfoContent,
                modifier           = Modifier.size(14.dp)
            )
            Text(
                text       = "설정",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize   = 14.sp,
                color      = InfoContent
            )
        }

        Text(
            text       = "장치 등록",
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.ExtraBold,
            fontSize   = 17.sp,
            color      = NeutralText,
            modifier   = Modifier.align(Alignment.Center)
        )
    }
}

// ─── 등록 폼 ──────────────────────────────────────────────────────────────────

@Composable
private fun FormContent(
    form: RegisterFormState,
    isLoading: Boolean,
    apiError: String?,
    onSerialChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onRegister: () -> Unit,
    onDismissError: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 안내 카드
        GuideCard()

        // 입력 폼
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 시리얼 번호
            RegisterField(
                label       = "시리얼 번호",
                icon        = Icons.Outlined.Memory,
                value       = form.deviceSerial,
                placeholder = "예) JTN-20240812-001",
                error       = form.serialError,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    keyboardType   = KeyboardType.Ascii,
                    imeAction      = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                onValueChange = onSerialChange
            )

            HorizontalDivider(color = Color(0xFFF0F2F5))

            // 장치 이름
            RegisterField(
                label       = "장치 이름",
                icon        = Icons.Outlined.Videocam,
                value       = form.deviceName,
                placeholder = "예) 아기방 카메라",
                error       = form.nameError,
                counter     = "${form.deviceName.length}/20",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                onValueChange = onNameChange
            )

            HorizontalDivider(color = Color(0xFFF0F2F5))

            // 설치 위치
            RegisterField(
                label       = "설치 위치",
                icon        = Icons.Outlined.LocationOn,
                value       = form.locationName,
                placeholder = "예) 아기 침실 천장",
                error       = form.locationError,
                counter     = "${form.locationName.length}/100",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus(); onRegister() }
                ),
                onValueChange = onLocationChange
            )
        }

        // API 에러 메시지
        if (apiError != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DangerContent.copy(alpha = 0.08f))
                    .clickable { onDismissError() }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector        = Icons.Outlined.Info,
                    contentDescription = null,
                    tint               = DangerContent,
                    modifier           = Modifier.size(18.dp)
                )
                Text(
                    text       = apiError,
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.Normal,
                    fontSize   = 13.sp,
                    color      = DangerContent,
                    modifier   = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 등록 버튼
        Button(
            onClick  = { focusManager.clearFocus(); onRegister() },
            enabled  = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(52.dp),
            shape  = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color    = Color.White,
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text       = "장치 등록",
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = Color.White
                )
            }
        }
    }
}

// ─── 안내 카드 ────────────────────────────────────────────────────────────────

@Composable
private fun GuideCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BrandPrimary.copy(alpha = 0.07f))
            .border(
                width = 1.dp,
                color = BrandPrimary.copy(alpha = 0.18f),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 14.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(BrandPrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Outlined.Info,
                contentDescription = null,
                tint               = BrandPrimary,
                modifier           = Modifier.size(18.dp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text       = "등록 전에 확인하세요",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize   = 13.sp,
                color      = BrandPrimary
            )
            listOf(
                "장치에 전원이 연결되어 있어야 합니다",
                "장치가 인터넷에 연결된 상태여야 합니다",
                "시리얼 번호는 장치 하단 스티커에서 확인할 수 있습니다"
            ).forEach { guide ->
                Text(
                    text       = "• $guide",
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.Normal,
                    fontSize   = 12.sp,
                    color      = BrandPrimary.copy(alpha = 0.75f)
                )
            }
        }
    }
}

// ─── 입력 필드 ────────────────────────────────────────────────────────────────

@Composable
private fun RegisterField(
    label: String,
    icon: ImageVector,
    value: String,
    placeholder: String,
    error: String?,
    counter: String? = null,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    onValueChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = NeutralSubText,
                modifier           = Modifier.size(16.dp)
            )
            Text(
                text       = label,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize   = 13.sp,
                color      = NeutralSubText
            )
        }

        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            placeholder   = {
                Text(
                    text       = placeholder,
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.Normal,
                    fontSize   = 14.sp,
                    color      = NeutralSubText.copy(alpha = 0.5f)
                )
            },
            isError         = error != null,
            singleLine      = true,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = BrandPrimary,
                unfocusedBorderColor = Color(0xFFE0E4EA),
                errorBorderColor     = DangerContent,
                focusedTextColor     = NeutralText,
                unfocusedTextColor   = NeutralText,
                cursorColor          = BrandPrimary
            ),
            shape    = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (error != null) {
                Text(
                    text       = error,
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.Normal,
                    fontSize   = 11.sp,
                    color      = DangerContent
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
            if (counter != null) {
                Text(
                    text       = counter,
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.Normal,
                    fontSize   = 11.sp,
                    color      = NeutralSubText.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// ─── 등록 완료 ────────────────────────────────────────────────────────────────

@Composable
private fun SuccessContent(
    device: RegisterDeviceData,
    onGoToDevice: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 성공 아이콘
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(SafeCardAccent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint               = SafeCardAccent,
                modifier           = Modifier.size(44.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text       = "장치 등록 완료",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 20.sp,
                color      = NeutralText
            )
            Text(
                text       = "장치가 성공적으로 등록되었습니다",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Normal,
                fontSize   = 14.sp,
                color      = NeutralSubText
            )
        }

        // 등록된 장치 정보
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        ) {
            listOf(
                "장치 이름"    to device.deviceName,
                "설치 위치"    to device.locationName,
                "시리얼 번호"  to device.deviceSerialNumber
            ).forEachIndexed { index, (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text       = label,
                        fontFamily = NanumSquareRound,
                        fontWeight = FontWeight.Normal,
                        fontSize   = 14.sp,
                        color      = NeutralSubText
                    )
                    Text(
                        text       = value,
                        fontFamily = NanumSquareRound,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 14.sp,
                        color      = NeutralText
                    )
                }
                if (index != 2) {
                    HorizontalDivider(
                        color    = Color(0xFFF0F2F5),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 버튼
        Column(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick  = onGoToDevice,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Text(
                    text       = "장치 상태 확인하기",
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = Color.White
                )
            }

            Button(
                onClick  = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF2F4F7)
                )
            ) {
                Text(
                    text       = "설정으로 돌아가기",
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = NeutralText
                )
            }
        }
    }
}

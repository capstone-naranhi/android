package com.example.android.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android.R
import com.example.android.data.network.SessionRepository
import com.example.android.ui.theme.AndroidTheme
import com.example.android.ui.theme.AppBackground
import com.example.android.ui.theme.BrandPrimary
import com.example.android.ui.theme.DangerContent
import com.example.android.ui.theme.NanumSquareRound
import com.example.android.ui.theme.NeutralSubText
import com.example.android.ui.theme.NeutralSurface
import com.example.android.ui.theme.NeutralText
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val repository = remember { SessionRepository() }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("login_prefs", android.content.Context.MODE_PRIVATE) }

    var saveId by remember { mutableStateOf(prefs.getBoolean("save_id", false)) }
    var username by remember { mutableStateOf(if (prefs.getBoolean("save_id", false)) prefs.getString("saved_username", "") ?: "" else "") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun attemptLogin() {
        if (username.isBlank() || password.isBlank()) {
            errorMessage = "아이디와 비밀번호를 입력해주세요."
            return
        }
        scope.launch {
            isLoading = true
            errorMessage = null
            focusManager.clearFocus()
            repository.login(username.trim(), password)
                .onSuccess {
                    prefs.edit()
                        .putBoolean("save_id", saveId)
                        .putString("saved_username", if (saveId) username.trim() else "")
                        .apply()
                    onLoginSuccess()
                }
                .onFailure { errorMessage = it.message ?: "로그인 중 오류가 발생했습니다." }
            isLoading = false
        }
    }

    Scaffold(containerColor = AppBackground) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ─── 로고 ─────────────────────────────────────────────────────
            Image(
                painter = painterResource(id = R.drawable.logo_ibom),
                contentDescription = "아이봄 로고",
                modifier = Modifier.size(160.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // ─── 설명 문구 ─────────────────────────────────────────────────
            Text(
                text = "육아 부담을 줄여주는 돌봄 솔루션",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = NeutralSubText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ─── 아이디 ────────────────────────────────────────────────────
            LoginTextField(
                value = username,
                onValueChange = { username = it; errorMessage = null },
                label = "아이디",
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Person,
                        contentDescription = null,
                        tint = NeutralSubText,
                        modifier = Modifier.size(20.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            // ─── 비밀번호 ──────────────────────────────────────────────────
            LoginTextField(
                value = password,
                onValueChange = { password = it; errorMessage = null },
                label = "비밀번호",
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = NeutralSubText,
                        modifier = Modifier.size(20.dp)
                    )
                },
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff
                                          else Icons.Outlined.Visibility,
                            contentDescription = if (passwordVisible) "비밀번호 숨기기" else "비밀번호 보기",
                            tint = NeutralSubText,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { attemptLogin() })
            )

            // ─── 아이디 저장 ────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = saveId,
                    onCheckedChange = { saveId = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = BrandPrimary,
                        uncheckedColor = NeutralSubText
                    )
                )
                Text(
                    text = "아이디 저장",
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = NeutralSubText
                )
            }

            // ─── 에러 메시지 ────────────────────────────────────────────────
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DangerContent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ─── 로그인 버튼 ────────────────────────────────────────────────
            Button(
                onClick = { attemptLogin() },
                enabled = !isLoading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text(
                        text = "로그인",
                        fontFamily = NanumSquareRound,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ─── 공통 입력 필드 ────────────────────────────────────────────────────────────

@Composable
private fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = NeutralText,
            unfocusedTextColor = NeutralText,
            focusedBorderColor = BrandPrimary,
            unfocusedBorderColor = NeutralSurface,
            focusedLabelColor = BrandPrimary,
            unfocusedLabelColor = NeutralSubText,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = BrandPrimary
        ),
        modifier = modifier.fillMaxWidth()
    )
}

// ─── 프리뷰 ────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    AndroidTheme {
        LoginScreen(onLoginSuccess = {})
    }
}

package com.example.android.data.network

import com.example.android.data.model.HomeData

class HomeRepository {

    private val api get() = RetrofitClient.apiService

    suspend fun getHome(): Result<HomeData> = runCatching {
        val response = api.getHome()
        if (!response.isSuccessful) {
            val msg = when (response.code()) {
                401 -> "로그인이 필요합니다."
                403 -> "접근 권한이 없습니다."
                else -> "홈 데이터 로드 실패 (${response.code()})"
            }
            error(msg)
        }
        val body = response.body()
        if (body?.success != true || body.data == null) {
            error(body?.error?.message ?: "데이터를 불러올 수 없습니다.")
        }
        body.data
    }
}

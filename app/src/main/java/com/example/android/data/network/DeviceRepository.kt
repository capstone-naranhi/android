package com.example.android.data.network

import com.example.android.data.model.DeviceDetailData
import com.example.android.data.model.RegisterDeviceData
import com.example.android.data.model.RegisterDeviceRequest

class DeviceRepository {
    private val api = RetrofitClient.apiService

    suspend fun registerDevice(request: RegisterDeviceRequest): Result<RegisterDeviceData> {
        return try {
            val response = api.registerDevice(request)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()!!.data
                if (data != null) Result.success(data)
                else Result.failure(Exception("데이터 없음"))
            } else {
                val errorMsg = response.body()?.error?.message ?: "등록에 실패했습니다"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDeviceDetail(deviceId: Long): Result<DeviceDetailData> {
        return try {
            val response = api.getDeviceDetail(deviceId)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()!!.data
                if (data != null) Result.success(data)
                else Result.failure(Exception("데이터 없음"))
            } else {
                val errorMsg = response.body()?.error?.message ?: "알 수 없는 오류"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

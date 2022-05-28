package com.example.dailyexpenses.repository

import android.util.Log
import com.example.dailyexpenses.api.ApiResponse
import com.example.dailyexpenses.api.FcmResponse
import com.example.dailyexpenses.api.FirebaseCloudMessagingApi
import com.example.dailyexpenses.api.NotificationSender
import javax.inject.Inject

class FirebaseDataSource @Inject constructor(private val fcmApi: FirebaseCloudMessagingApi) {

    suspend fun sendNotification(notificationSender: NotificationSender): ApiResponse<FcmResponse>{
        return try {
            val response = fcmApi.sendNotification(notificationSender)
            ApiResponse.Success(response.body()!!)
        }catch (e: Exception){
            Log.d("Error", e.message.toString())
            ApiResponse.Error(e)
        }
    }
}
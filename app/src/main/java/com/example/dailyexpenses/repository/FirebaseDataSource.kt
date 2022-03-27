package com.example.dailyexpenses.repository

import com.example.dailyexpenses.api.FirebaseCloudMessagingApi
import com.example.dailyexpenses.api.NotificationSender
import javax.inject.Inject

class FirebaseDataSource @Inject constructor(private val fcmApi: FirebaseCloudMessagingApi) {

    suspend fun sendNotification(notificationSender: NotificationSender) = fcmApi.sendNotification(notificationSender)
}
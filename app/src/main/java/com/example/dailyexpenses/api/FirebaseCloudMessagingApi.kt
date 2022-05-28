package com.example.dailyexpenses.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FirebaseCloudMessagingApi {
    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAAKoxMm5g:APA91bE2_8ctzW0opDHGE_3F0L_W5OxhYkViPuZUIBp9id8aHbObbYH5UcmoX42tuLgsq0ktK1iq1UICdaG7e4MKv8GK6O32fcPHbLOrBMjPZdYE4UBYXNt0wQFyPc7OyKgh7lpA9xOA"
    )
    @POST("fcm/send")
    suspend fun sendNotification(@Body body: NotificationSender?): Response<FcmResponse>

    companion object {
        const val BASE_URL = "https://fcm.googleapis.com/"
    }
}
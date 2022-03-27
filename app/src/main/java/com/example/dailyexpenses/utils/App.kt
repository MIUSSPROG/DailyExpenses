package com.example.dailyexpensespredprof.utils
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.dailyexpenses.utils.Prefs
import dagger.hilt.android.HiltAndroidApp


val prefs by lazy {
    Prefs(App.instance)
}

@HiltAndroidApp
class App : Application() {
    companion object{
        lateinit var instance: App
        const val CHANNEL_ID = "myChannelId"
        const val CHANNEL_NAME = "myChannel"
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
//        createNotificationChannel()
    }

//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val serviceChannel = NotificationChannel(
//                CHANNEL_ID,
//                CHANNEL_NAME,
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            val manager = getSystemService(
//                NotificationManager::class.java
//            )
//            manager.createNotificationChannel(serviceChannel)
//        }
//    }
}
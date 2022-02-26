package com.example.dailyexpensespredprof.utils
import android.app.Application
import com.example.dailyexpenses.utils.Prefs
import dagger.hilt.android.HiltAndroidApp


val prefs by lazy {
    Prefs(App.instance)
}

@HiltAndroidApp
class App : Application() {
    companion object{
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
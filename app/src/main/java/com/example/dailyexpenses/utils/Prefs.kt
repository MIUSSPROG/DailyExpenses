package com.example.dailyexpenses.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class Prefs(context: Context) {

    companion object{
        private const val PREFS_FILENAME = "myPrefs"
        private const val KEY_SIGNED_IN = "isSignedIn"
        private const val KEY_LOGIN = "login"
        private const val KEY_PASS = "pass"
    }

    private val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    var isSignedIn: Boolean
        get() = sharedPref.getBoolean(KEY_SIGNED_IN, false) ?: false
        set(value) = sharedPref.edit{ putBoolean(KEY_SIGNED_IN, value)}

    var login: String
        get() = sharedPref.getString(KEY_LOGIN, "") ?: ""
        set(value) = sharedPref.edit { putString(KEY_LOGIN, value) }

    var pass: String
        get() = sharedPref.getString(KEY_PASS, "") ?: ""
        set(value) = sharedPref.edit { putString(KEY_PASS, value) }

}
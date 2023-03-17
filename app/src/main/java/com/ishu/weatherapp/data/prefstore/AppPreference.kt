package com.ishu.weatherapp.data.prefstore

import android.content.Context
import androidx.preference.PreferenceManager

import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreference @Inject constructor(@ApplicationContext context : Context){

    val CITY_SEARCH = "CITY_SEARCH"

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    fun getStoredTag(query: String): String {
        return prefs.getString(query, "")!!
    }
    fun setStoredTag(query: String,value: String) {
        prefs.edit().putString(query, value).apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

}
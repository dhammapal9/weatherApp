package com.ishu.weatherapp.data.remote

import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val contentService: ContentService
) {
    suspend fun getWeatherAvailable(
        city: String,
        stateCode: String,
        countryCode: String
    ) =
        contentService.getWeatherAvailable(
            city,
            stateCode,
            countryCode
        )

}
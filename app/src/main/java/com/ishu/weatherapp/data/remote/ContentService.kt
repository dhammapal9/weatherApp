package com.ishu.weatherapp.data.remote

import com.ishu.weatherapp.data.models.GenericResponse
import com.ishu.weatherapp.utils.ApiHandle
import retrofit2.Response
import retrofit2.http.*

interface ContentService {

    @GET(ApiHandle.base_weather_api)
    suspend fun getWeatherAvailable(
        @Query("q") city: String?,
        @Query("stateCode") stateCode: String?,
        @Query("countryCode") countryCode: String?
    ): Response<GenericResponse>
}
package com.ishu.weatherapp.ui.home.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishu.weatherapp.data.models.GenericResponse
import com.ishu.weatherapp.data.repository.AppRepository
import com.ishu.weatherapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor
    (
    private val repository: AppRepository,
    application: Application
) : ViewModel() {

    private val _response: MutableLiveData<NetworkResult<GenericResponse>> = MutableLiveData()
    val response: LiveData<NetworkResult<GenericResponse>> = _response

    fun getWeatherAvailable(city:String,
                            stateCode:String,
                            countryCode: String
    ) = viewModelScope.launch {
        repository.getWeatherAvailable(
            city,
            stateCode,
            countryCode
        ).collect { values ->
            _response.value = values
        }
    }

}
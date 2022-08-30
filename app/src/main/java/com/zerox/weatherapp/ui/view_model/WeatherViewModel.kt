package com.zerox.weatherapp.ui.view_model

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zerox.weatherapp.data.model.entites.weather.WeatherResponse
import com.zerox.weatherapp.data.network.WeatherService
import com.zerox.weatherapp.ui.view_model.exceptions.FailedApiResponseException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherService: WeatherService
):ViewModel(){
    val weatherModel = MutableLiveData<WeatherResponse?>()

    suspend fun getWeather(url:String){
        val weatherResponse = weatherService.getWeather(url)
        if (weatherResponse != null)
            weatherModel.postValue(weatherResponse)
        else throw FailedApiResponseException()
    }
}
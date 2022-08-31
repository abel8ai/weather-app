package com.zerox.weatherapp.ui.view_model

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zerox.weatherapp.data.model.entites.weather.WeatherResponse
import com.zerox.weatherapp.data.network.WeatherService
import com.zerox.weatherapp.ui.view.MainActivity
import com.zerox.weatherapp.ui.view_model.exceptions.FailedApiResponseException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherService: WeatherService
):ViewModel(){
    val weatherModel = MutableLiveData<WeatherResponse?>()

    suspend fun getWeatherByCoordinates(apiKey:String,latitude: Double, longitude:Double){
        val weatherResponse = weatherService.getWeather("weather?appid=$apiKey&lat=$latitude&lon=$longitude&units=metric")
        if (weatherResponse != null)
            weatherModel.postValue(weatherResponse)
        else throw FailedApiResponseException()
    }
    suspend fun getWeatherByCity(apiKey: String, city:String){
        val weatherResponse = weatherService.getWeather("weather?appid=$apiKey&q=$city&units=metric")
        if (weatherResponse != null)
            weatherModel.postValue(weatherResponse)
        else throw FailedApiResponseException()
    }
    fun getIcon(iconId:String):String{
        return "http://openweathermap.org/img/wn/$iconId@2x.png"
    }
}
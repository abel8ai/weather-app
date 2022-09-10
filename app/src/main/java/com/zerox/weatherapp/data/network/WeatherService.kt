package com.zerox.weatherapp.data.network

import com.zerox.weatherapp.data.model.entites.weather.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import javax.inject.Inject

class WeatherService @Inject constructor(private val weatherApiClient: WeatherApiClient) {

    suspend fun getWeather(url:String):WeatherResponse?{
        return withContext(Dispatchers.IO){
            val response = weatherApiClient.getWeatherData(url)
            response.body()
        }
    }
}
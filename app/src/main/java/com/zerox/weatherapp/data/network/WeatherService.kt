package com.zerox.weatherapp.data.network

import com.zerox.weatherapp.data.model.entites.weather.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import javax.inject.Inject

class WeatherService @Inject constructor(private val retrofit: Retrofit) {

    suspend fun getWeather(url:String):WeatherResponse?{
        return withContext(Dispatchers.IO){
            val response = retrofit.create(WeatherApiClient::class.java).getWeatherData(url)
            response.body()
        }
    }
}
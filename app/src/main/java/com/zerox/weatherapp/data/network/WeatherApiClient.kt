package com.zerox.weatherapp.data.network

import com.zerox.weatherapp.data.model.entites.weather.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface WeatherApiClient {
    @GET
    suspend fun getWeatherData(@Url url: String):Response<WeatherResponse>
}
package com.zerox.randomuserapp.ui.view_model


import android.os.Build.VERSION_CODES.Q

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.zerox.weatherapp.data.network.WeatherService
import com.zerox.weatherapp.ui.view_model.WeatherViewModel
import com.zerox.weatherapp.ui.view_model.exceptions.FailedApiResponseException
import com.zerox.weatherapp.ui.view_model.getorAwaitValue
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Q], manifest = "src/main/AndroidManifest.xml", packageName = "com.zerox.randomuserapp")
internal class WeatherViewModelTest{

    @MockK
    private lateinit var weatherService: WeatherService
    private lateinit var weatherViewModel: WeatherViewModel
    private val url = "?results=50&seed=abc&page=1&inc=name,email,picture,location"
    private val email = "laura.woods@example.com"


    @Before
    fun initialize(){
        MockKAnnotations.init(this)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://randomuser.me/api/1.4/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        weatherService = WeatherService(retrofit)
        weatherViewModel = WeatherViewModel(weatherService)
        weatherService = mockk()
    }
    @Test
    fun shouldReturnWeatherDataGivenCoordinates() = runBlocking {
        // given
        val url = "weather?appid=9dbf9e303c0cfbcd910c6a20a5d9b0af&lat=23.113592&lon=-82.366592"
        val weatherResponse = weatherService.getWeather(url)!!
        coEvery { weatherService.getWeather(url)} returns weatherResponse
        // when
        weatherViewModel.getWeatherByCoordinates("9dbf9e303c0cfbcd910c6a20a5d9b0af",23.113592,-82.366592)
        val result = weatherViewModel.weatherModel.getorAwaitValue()
        // then
        coVerify(exactly = 1) { weatherService.getWeather(url) }
        Truth.assertThat(result == weatherResponse).isTrue()
    }
    @Test
    fun shouldReturnWeatherDataGivenCityName() = runBlocking {
        // given
        val url = "weather?appid=9dbf9e303c0cfbcd910c6a20a5d9b0af&q=Havana"
        val weatherResponse = weatherService.getWeather(url)!!
        coEvery { weatherService.getWeather(url)} returns weatherResponse
        // when
        weatherViewModel.getWeatherByCity("9dbf9e303c0cfbcd910c6a20a5d9b0af", "Havana")
        val result = weatherViewModel.weatherModel.getorAwaitValue()
        // then
        coVerify(exactly = 1) { weatherService.getWeather(url) }
        Truth.assertThat(result == weatherResponse).isTrue()
    }
    @Test
    fun shouldThrowFailedApiResponseExceptionOnNullResponseByCoordinate(): Unit = runBlocking {
        // given
        val url = "weather?appid=9dbf9e303c0cfbcd910c6a20a5d9b0af&lat=23.113592&lon=-82.366592"
        coEvery { weatherService.getWeather(url) }returns null
        // when
        weatherViewModel.getWeatherByCoordinates("9dbf9e303c0cfbcd910c6a20a5d9b0af",4.0,4.0)
        // then
        coVerify(exactly = 1) { weatherService.getWeather(url) }
        Assert.assertThrows(FailedApiResponseException::class.java){
            runBlocking { weatherViewModel.getWeatherByCoordinates("9dbf9e303c0cfbcd910c6a20a5d9b0af",4.0,4.0) }
        }
    }
    @Test
    fun shouldThrowFailedApiResponseExceptionOnNullResponseByCityName(): Unit = runBlocking {
        // given
        val url = "weather?appid=9dbf9e303c0cfbcd910c6a20a5d9b0af&q=Havana"
        coEvery { weatherService.getWeather(url) }returns null
        // when
        weatherViewModel.getWeatherByCity("9dbf9e303c0cfbcd910c6a20a5d9b0af","Havana")
        // then
        coVerify(exactly = 1) { weatherService.getWeather(url) }
        Assert.assertThrows(FailedApiResponseException::class.java){
            runBlocking { weatherViewModel.getWeatherByCity("9dbf9e303c0cfbcd910c6a20a5d9b0af","Havana") }
        }
    }
}
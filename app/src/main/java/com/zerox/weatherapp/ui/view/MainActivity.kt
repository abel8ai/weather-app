package com.zerox.weatherapp.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.squareup.picasso.Picasso
import com.zerox.weatherapp.data.model.entites.weather.WeatherResponse
import com.zerox.weatherapp.databinding.ActivityMainBinding
import com.zerox.weatherapp.ui.view_model.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val weatherViewModel: WeatherViewModel by viewModels()
    private val WEATHER_API_KEY = "9dbf9e303c0cfbcd910c6a20a5d9b0af"
    private lateinit var weatherData: WeatherResponse
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // observer to update view once the data is retrieved from api
        weatherViewModel.weatherModel.observe(this, Observer {
            weatherData = it!!
            showWeatherData()
        })
        loadData()
    }

    private fun showWeatherData() {
        // show weather conditions and temp
        binding.tvCity.text = weatherData.name
        binding.tvTemp.text = weatherData.main.temp.toString()
        val imageUri = weatherViewModel.getIcon(weatherData.weather[0].icon)
        Picasso.get().load(imageUri).into(binding.ivWeatherImage)

        // show first section data retrieved from api
        binding.tvPressure.text = weatherData.main.pressure.toString()
        binding.tvHumidity.text = weatherData.main.humidity.toString()
        binding.tvVisibility.text = weatherData.visibility.toString()
        binding.tvSeaLevel.text = weatherData.main.sea_level.toString()

        // show wind data retrieved from api
        binding.tvWindSpeed.text = weatherData.wind.speed.toString()
        binding.tvWindDegree.text = weatherData.wind.deg.toString()
        binding.tvWindGusts.text = weatherData.wind.gust.toString()

    }

    private fun loadData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                weatherViewModel.getWeather("weather?appid=${WEATHER_API_KEY}&lat=35&lon=139&units=metric")
            } catch (exception: Exception) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity,exception.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
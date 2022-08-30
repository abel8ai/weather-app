package com.zerox.weatherapp.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
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
        weatherViewModel.weatherModel.observe(this, Observer {
            weatherData = it!!
        })
        loadData()
    }

    private fun loadData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                weatherViewModel.getWeather("weather?appid=${WEATHER_API_KEY}&lat=35&lon=139")
            } catch (exception: Exception) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity,exception.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
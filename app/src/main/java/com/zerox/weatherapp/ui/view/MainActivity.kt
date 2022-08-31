package com.zerox.weatherapp.ui.view

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
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
    private lateinit var weatherData: WeatherResponse
    private lateinit var binding: ActivityMainBinding

    // default location in case the user doesn't give location permissions (Havana)
    private val defaultLocation = LatLng(23.113592, -82.366592)
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null
    private var locationPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // listener to update weather data when the user submits query
        binding.svCountry.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null){
                    binding.pbLoadingData.visibility = View.VISIBLE
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            weatherViewModel.getWeatherByCity(WEATHER_API_KEY,query)
                        } catch (exception: Exception) {
                            runOnUiThread {
                                Toast.makeText(this@MainActivity, exception.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
        }
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLocationPermission()

        // observer to update view once the data is retrieved from api
        weatherViewModel.weatherModel.observe(this, Observer {
            weatherData = it!!
            binding.pbLoadingData.visibility = View.INVISIBLE
            showWeatherData()
        })
    }

    private fun showWeatherData() {
        // show weather conditions and temp
        binding.tvCity.text = weatherData.name
        binding.tvTemp.text = weatherData.main.temp.toInt().toString()

        // get weather icon
        val imageUri = weatherViewModel.getIcon(weatherData.weather[0].icon)
        Picasso.get().load(imageUri).into(binding.ivWeatherImage)

        // set current location in search bar
        binding.svCountry.queryHint = weatherData.sys.country

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

    private fun loadWeatherFromLocation(latitude: Double, longitude: Double) {
        binding.pbLoadingData.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                weatherViewModel.getWeatherByCoordinates(WEATHER_API_KEY, latitude, longitude)
            } catch (exception: Exception) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, exception.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
            getDeviceLocation()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        getDeviceLocation()
    }

    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            loadWeatherFromLocation(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        loadWeatherFromLocation(defaultLocation.latitude, defaultLocation.longitude)
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private const val KEY_LOCATION = "location"
        private const val WEATHER_API_KEY = "9dbf9e303c0cfbcd910c6a20a5d9b0af"
    }
}
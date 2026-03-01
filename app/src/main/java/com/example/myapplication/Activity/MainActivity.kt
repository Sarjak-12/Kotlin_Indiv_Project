package com.example.myapplication.Activity

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.viewmodel.WeatherViewModel
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    private val viewModel: WeatherViewModel by viewModels()

    private lateinit var cityEditText: TextInputEditText
    private lateinit var searchButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var weatherScrollView: ScrollView
    private lateinit var errorTextView: TextView

    // Weather info views
    private lateinit var cityNameTextView: TextView
    private lateinit var countryTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var feelsLikeTextView: TextView
    private lateinit var minMaxTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var pressureTextView: TextView
    private lateinit var windSpeedTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupObservers()
        setupListeners()
    }

    private fun initializeViews() {
        cityEditText = findViewById(R.id.cityEditText)
        searchButton = findViewById(R.id.searchButton)
        progressBar = findViewById(R.id.progressBar)
        weatherScrollView = findViewById(R.id.weatherScrollView)
        errorTextView = findViewById(R.id.errorTextView)

        cityNameTextView = findViewById(R.id.cityNameTextView)
        countryTextView = findViewById(R.id.countryTextView)
        temperatureTextView = findViewById(R.id.temperatureTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        feelsLikeTextView = findViewById(R.id.feelsLikeTextView)
        minMaxTextView = findViewById(R.id.minMaxTextView)
        humidityTextView = findViewById(R.id.humidityTextView)
        pressureTextView = findViewById(R.id.pressureTextView)
        windSpeedTextView = findViewById(R.id.windSpeedTextView)
    }

    private fun setupObservers() {
        viewModel.weatherData.observe(this) { weather ->
            weather?.let {
                displayWeatherData(it)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
                weatherScrollView.visibility = View.GONE
                errorTextView.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                errorTextView.text = it
                errorTextView.visibility = View.VISIBLE
                weatherScrollView.visibility = View.GONE
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupListeners() {
        searchButton.setOnClickListener {
            val city = cityEditText.text.toString().trim()
            if (city.isEmpty()) {
                cityEditText.error = "Please enter a city name"
                return@setOnClickListener
            }

            // Hide keyboard
            hideKeyboard()

            // Clear previous error
            viewModel.clearError()

            // Fetch weather
            viewModel.getWeather(city)
        }

        // Also allow search on Enter key
        cityEditText.setOnEditorActionListener { _, _, _ ->
            searchButton.performClick()
            true
        }
    }

    private fun displayWeatherData(weather: com.example.myapplication.model.WeatherResponse) {
        weatherScrollView.visibility = View.VISIBLE
        errorTextView.visibility = View.GONE

        cityNameTextView.text = weather.name
        countryTextView.text = weather.sys.country

        // Temperature
        val temp = weather.main.temp.toInt()
        temperatureTextView.text = "$temp°C"

        // Description (capitalize first letter of each word)
        val description = weather.weather.firstOrNull()?.description?.split(" ")
            ?.joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } } ?: ""
        descriptionTextView.text = description

        // Feels like
        val feelsLike = weather.main.feelsLike.toInt()
        feelsLikeTextView.text = "$feelsLike°C"

        // Min/Max temperature
        val minTemp = weather.main.tempMin.toInt()
        val maxTemp = weather.main.tempMax.toInt()
        minMaxTextView.text = "$minTemp°C / $maxTemp°C"

        // Humidity
        humidityTextView.text = "${weather.main.humidity}%"

        // Pressure
        pressureTextView.text = "${weather.main.pressure} hPa"

        // Wind speed
        windSpeedTextView.text = "${weather.wind.speed} m/s"
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}
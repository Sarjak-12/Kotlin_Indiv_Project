package com.example.myapplication.repository

import com.example.myapplication.data.remote.RetrofitInstance

class WeatherRepository {
    suspend fun getWeather(city: String) =
        RetrofitInstance.api.getWeatherByCity(city, com.example.weatherapp.utils.Constants.API_KEY)
}
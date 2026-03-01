package com.example.myapplication.repository

import com.example.myapplication.remote.RetrofitInstance
import com.example.myapplication.utils.Constants

class WeatherRepository {
    suspend fun getWeather(city: String) =
        RetrofitInstance.api.getWeatherByCity(city, Constants.API_KEY)
}
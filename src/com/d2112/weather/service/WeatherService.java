package com.d2112.weather.service;

import com.d2112.weather.model.Forecast;

import java.util.List;

public interface WeatherService {

    List<Forecast> getWeekForecast(String cityName);
}

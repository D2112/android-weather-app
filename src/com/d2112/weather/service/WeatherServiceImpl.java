package com.d2112.weather.service;

import com.d2112.weather.model.Forecast;
import com.d2112.weather.model.Temperature;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeatherServiceImpl implements WeatherService {

    @Override
    public List<Forecast> getForecastForFewWeeks() {
        Temperature temperature = new Temperature(12);
        Temperature temperature1 = new Temperature(16);
        List<Temperature> temperatureList = new ArrayList<>();
        temperatureList.add(temperature);
        temperatureList.add(temperature1);
        Forecast forecast = new Forecast(new Date(), temperatureList);
        List<Forecast> forecastList = new ArrayList<>();
        forecastList.add(forecast);
        return forecastList;
    }
}

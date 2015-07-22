package com.d2112.weather.service;

import android.os.AsyncTask;
import android.util.Log;
import com.d2112.weather.Files;
import com.d2112.weather.model.Forecast;
import com.d2112.weather.model.Temperature;
import com.d2112.weather.model.Wind;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WeatherServiceImpl implements WeatherService {
    private static final String TAG = WeatherServiceImpl.class.getSimpleName();
    private static final String ASCII_SPACE_SYMBOL = "%20";
    private static final String APPID_PARAMETER = "&APPID=6b01766ea5092223106066a52a9cc3ea";
    private static final String FORECAST_FOR_WEEK_URL_WITHOUT_CITY_PARAMETER = "http://api.openweathermap.org/data/2.5/forecast/daily?units=metric&q=";
    private static final int UNIX_TIME_TO_MILLISECONDS_MULTIPLIER = 1000;
    private Gson gson;

    public WeatherServiceImpl() {
        gson = new Gson();
    }

    @Override
    public List<Forecast> getWeekForecast(String cityName) {
        String urlWithCityName = prepareUrl(FORECAST_FOR_WEEK_URL_WITHOUT_CITY_PARAMETER, cityName);
        String jsonRequestResult = ""; //will be returned empty string if request failed
        try {
            jsonRequestResult = new RetrieveJsonWeekForecastTask().execute(urlWithCityName).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Error during execution async task: " + RetrieveJsonWeekForecastTask.class.getName(), e);
        }
        return parseJsonForecast(jsonRequestResult);
    }

    private List<Forecast> parseJsonForecast(String jsonForecast) {
        if (jsonForecast == null || jsonForecast.isEmpty()) {
            throw new IllegalArgumentException("String with json forecast to parsing is null or empty");
        }
        List<Forecast> forecastList = new ArrayList<>();
        JsonObject jsonResult = gson.fromJson(jsonForecast, JsonObject.class);
        String cityName = jsonResult.get("city").getAsJsonObject().get("name").getAsString();
        JsonArray dayForecastList = jsonResult.getAsJsonArray("list");

        for (JsonElement jsonElement : dayForecastList) {
            JsonObject dayForecast = jsonElement.getAsJsonObject();
            int windDegree = dayForecast.get("deg").getAsInt();
            int humidity = dayForecast.get("humidity").getAsInt();
            long dateMilliseconds = dayForecast.get("dt").getAsLong();
            double windSpeed = dayForecast.get("speed").getAsDouble();
            JsonObject weather = dayForecast.get("weather").getAsJsonArray().iterator().next().getAsJsonObject();
            String iconCode = weather.get("icon").getAsString();
            String description = weather.get("description").getAsString();

            JsonObject temp = dayForecast.get("temp").getAsJsonObject();
            double dayTemperature = temp.get("day").getAsDouble();
            double nightTemperature = temp.get("night").getAsDouble();
            double eveningTemperature = temp.get("eve").getAsDouble();
            double morningTemperature = temp.get("morn").getAsDouble();

            Forecast.Builder builder = Forecast.newBuilder();
            Forecast forecast = builder.
                    setTemperature(Forecast.TimeOfDay.DAY, new Temperature(dayTemperature)).
                    setTemperature(Forecast.TimeOfDay.NIGHT, new Temperature(nightTemperature)).
                    setTemperature(Forecast.TimeOfDay.EVENING, new Temperature(eveningTemperature)).
                    setTemperature(Forecast.TimeOfDay.MORNING, new Temperature(morningTemperature)).
                    setWind(new Wind(windSpeed, windDegree)).
                    setDate(getDateFromMilliseconds(dateMilliseconds)).
                    setHumidity(humidity).
                    setIconCode(iconCode).
                    setCityName(cityName).
                    setDescription(description).
                    build();
            forecastList.add(forecast);
        }
        return forecastList;
    }

    private String prepareUrl(String url, String cityName) {
        //replace spaces by ASCII symbol to get a valid url
        String escapedFromSpacesCityName = cityName.replace(" ", ASCII_SPACE_SYMBOL);
        return url + escapedFromSpacesCityName + APPID_PARAMETER; //add city name and appid parameters to url
    }

    private Date getDateFromMilliseconds(long milliseconds) {
        return new Date(milliseconds * UNIX_TIME_TO_MILLISECONDS_MULTIPLIER);
    }

    private class RetrieveJsonWeekForecastTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                result = Files.convertStreamToString(conn.getInputStream());
            } catch (IOException e) {
                Log.e(TAG, "Exception when requesting a week forecast", e);
            }
            return result;
        }
    }
}

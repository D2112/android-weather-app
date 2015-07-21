package com.d2112.weather.service;

import android.util.Log;
import com.d2112.weather.model.Forecast;
import com.d2112.weather.model.Temperature;
import com.d2112.weather.model.Wind;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeatherServiceImpl implements WeatherService {
    private static final String TAG = WeatherServiceImpl.class.getSimpleName();
    private static final String FORECAST_FOR_WEEK_URL_WITHOUT_CITY_PARAMETER = "http://api.openweathermap.org/data/2.5/forecast/daily?units=metric&q=";
    private Gson gson;

    public WeatherServiceImpl() {
        gson = new Gson();
    }

    @Override
    public List<Forecast> getWeekForecast(String cityName) {
        String urlWithCityName = FORECAST_FOR_WEEK_URL_WITHOUT_CITY_PARAMETER + cityName;
        String jsonRequestResult = requestWeekForecast(urlWithCityName);
        return parseJsonForecast(jsonRequestResult);
    }

    private String requestWeekForecast(String urlString) {
        String result = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            result = convertStreamToString(conn.getInputStream());
        } catch (IOException e) {
            Log.e(TAG, "Exception when requesting a week forecast", e);
        }
        return result;
    }

    private String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A"); // The "A" token means beginning of the input boundary
        return s.hasNext() ? s.next() : ""; //if input isn't empty then return its content as a string
    }

    private List<Forecast> parseJsonForecast(String jsonForecast) {
        List<Forecast> forecastList = new ArrayList<>();
        JsonObject jsonResult = gson.fromJson(jsonForecast, JsonObject.class);
        JsonArray dayForecastList = jsonResult.getAsJsonArray("list");

        for (JsonElement jsonElement : dayForecastList) {
            JsonObject dayForecast = jsonElement.getAsJsonObject();
            int windDegree = dayForecast.get("deg").getAsInt();
            int humidity = dayForecast.get("humidity").getAsInt();
            long dateMilliseconds = dayForecast.get("dt").getAsLong();
            double windSpeed = dayForecast.get("speed").getAsDouble();
            String iconCode = dayForecast.get("weather").getAsJsonArray().iterator().next().getAsJsonObject().get("icon").getAsString();

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
                    build();

            forecastList.add(forecast);
        }
        return forecastList;
    }

    private Date getDateFromMilliseconds(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd-MM-yyyy");
        Date dateWithoutTime = null;
        try {
            dateWithoutTime = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            Log.e(TAG, "Error during parsing a date", e);
        }
        return dateWithoutTime == null ? new Date() : dateWithoutTime;
    }
}

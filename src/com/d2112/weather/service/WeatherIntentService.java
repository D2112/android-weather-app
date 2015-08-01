package com.d2112.weather.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import com.d2112.weather.Files;
import com.d2112.weather.WeatherApplication;
import com.d2112.weather.model.ClientLocation;
import com.d2112.weather.model.Forecast;
import com.d2112.weather.model.Temperature;
import com.d2112.weather.model.Wind;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WeatherIntentService extends IntentService {
    public static final int GET_WEEK_FORECAST_ACTION = 1;
    private static final String TAG = WeatherIntentService.class.getSimpleName();
    private static final String APPID_PARAMETER = "&APPID=6b01766ea5092223106066a52a9cc3ea";
    private static final int UNIX_TIME_TO_MILLISECONDS_MULTIPLIER = 1000;
    private static final String FORECAST_FOR_WEEK_BY_CITY_NAME_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?units=metric&q=";
    private static final String FORECAST_FOR_WEEK_BY_COORDINATES_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?units=metric&lat={lat}&lon={lon}";
    private static final String LATITUDE_VALUE_PARAMETER = "{lat}";
    private static final String LONGITUDE_VALUE_PARAMETER = "{lon}";
    private static final int MAX_STALE = 60 * 60 * 24; //caching time one day

    private Gson gson;

    public WeatherIntentService() {
        super("Weather Service");
        gson = new Gson();
    }

    private List<Forecast> getWeekForecast(ClientLocation clientLocation) throws LocationNotFoundException {
        Double latitude = clientLocation.getLatitude();
        Double longitude = clientLocation.getLongitude();
        //to get more precise location of forecast it's better to use coordinates than city name
        if (latitude != null && longitude != null) {
            return getWeekForecast(latitude, longitude);
        }
        String cityName = clientLocation.getCityName();
        if (cityName != null) {
            return getWeekForecast(cityName);
        }
        return null;
    }

    public List<Forecast> getWeekForecast(String cityName) throws LocationNotFoundException {
        String preparedUrl = getUrlForWeekForecastByCityName(cityName);
        String jsonResponse = executeHttpRequestGetTask(preparedUrl);
        return handleJsonResponse(jsonResponse);
    }

    public List<Forecast> getWeekForecast(double latitude, double longitude) throws LocationNotFoundException {
        String preparedUrl = getUrlForWeekForecastByCoordinates(latitude, longitude);
        String jsonResponse = executeHttpRequestGetTask(preparedUrl);
        return handleJsonResponse(jsonResponse);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int action = intent.getFlags();
        if (action == GET_WEEK_FORECAST_ACTION) {
            ResultReceiver resultReceiver = intent.getParcelableExtra(WeatherApplication.RESULT_RECEIVER_ARG_NAME);
            ClientLocation clientLocation = intent.getParcelableExtra(WeatherApplication.CLIENT_LOCATION_ARG_NAME);
            List<Forecast> forecastList = new ArrayList<>();
            try {
                forecastList = getWeekForecast(clientLocation);
            } catch (LocationNotFoundException e) {
                Log.w(TAG, "Location wasn't found: " + clientLocation, e);
                resultReceiver.send(Activity.RESULT_CANCELED, null);
            }
            if (forecastList == null || forecastList.size() == 0) {
                resultReceiver.send(Activity.RESULT_CANCELED, null);
                return;
            }
            //sending forecast list
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(WeatherApplication.FORECAST_LIST_ARG_NAME, new ArrayList<>(forecastList));
            resultReceiver.send(Activity.RESULT_OK, bundle);
        }
    }

    private List<Forecast> handleJsonResponse(String jsonResponse) throws LocationNotFoundException {
        if (jsonResponse == null || jsonResponse.isEmpty()) return new ArrayList<>();
        return parseJsonForecast(jsonResponse);
    }

    private String getUrlForWeekForecastByCoordinates(double latitude, double longitude) {
        String urlWithLatitude = FORECAST_FOR_WEEK_BY_COORDINATES_URL.replace(LATITUDE_VALUE_PARAMETER, String.valueOf(latitude));
        String urlWithLatAndLon = urlWithLatitude.replace(LONGITUDE_VALUE_PARAMETER, String.valueOf(longitude));
        return urlWithLatAndLon + APPID_PARAMETER;
    }

    private String getUrlForWeekForecastByCityName(String cityName) {
        //uppercase is needed for better recognizing of city name by service
        String cityNameWithUpperCasedFirstLetter = upperCaseFirstLetterOfCityName(cityName);
        return FORECAST_FOR_WEEK_BY_CITY_NAME_URL
                + encodeParameter(cityNameWithUpperCasedFirstLetter)
                + APPID_PARAMETER;
    }

    private String executeHttpRequestGetTask(String url) {
        try {
            return new SendHttpGetRequestTask().execute(url).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Error during execution async task: "
                    + SendHttpGetRequestTask.class.getName()
                    + ", with url: " + url, e);
        }
        return null;
    }

    private List<Forecast> parseJsonForecast(String jsonForecast) throws LocationNotFoundException {
        if (jsonForecast == null || jsonForecast.isEmpty()) {
            throw new IllegalArgumentException("String with json forecast to parsing must be not null or empty");
        }
        List<Forecast> forecastList = new ArrayList<>();

        JsonObject jsonResult = gson.fromJson(jsonForecast, JsonObject.class);
        int statusCode = jsonResult.get("cod").getAsInt();
        if (statusCode == 404) throw new LocationNotFoundException("Requested location is not found");

        String receivedCityName = jsonResult.get("city").getAsJsonObject().get("name").getAsString();
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
                    setCityName(receivedCityName).
                    setDescription(description).
                    build();
            forecastList.add(forecast);
        }
        return forecastList;
    }

    private String encodeParameter(String parameter) {
        try {
            parameter = URLEncoder.encode(parameter, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Error when encoding url parameter", e);
        }
        return parameter;
    }

    private Date getDateFromMilliseconds(long milliseconds) {
        return new Date(milliseconds * UNIX_TIME_TO_MILLISECONDS_MULTIPLIER);
    }

    private class SendHttpGetRequestTask extends AsyncTask<String, Void, String> {
        private static final int CONNECTION_TIMEOUT = 3000;

        @Override
        protected String doInBackground(String... urls) {
            String response = null;
            URL url = null;
            try {
                url = new URL(urls[0]);
                Log.d(TAG, "Starting to send http request with url: " + url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");
                conn.addRequestProperty("Cache-Control", "max-stale=" + MAX_STALE);
                InputStream in = conn.getInputStream();
                response = Files.convertStreamToString(in);
                in.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception when sending an http request with url: " + url + ", caused by:" + e, e);
            }
            return response;
        }
    }

    private String upperCaseFirstLetterOfCityName(String cityName) {
        char[] stringArray = cityName.trim().toCharArray();
        stringArray[0] = Character.toUpperCase(stringArray[0]);
        return new String(stringArray);
    }
}

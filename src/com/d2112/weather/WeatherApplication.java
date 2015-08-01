package com.d2112.weather;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import com.d2112.weather.model.ClientLocation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * The starting application class that decides which activity should be launched next
 * and does some additional stuff like cache saving, checking internet connection etc.
 */
public class WeatherApplication extends Application {
    public static final String CLIENT_LOCATION_ARG_NAME = "clientLocation";
    public static final String FORECAST_ARG_NAME = "forecast";
    public static final String FORECAST_LIST_ARG_NAME = "forecastList";
    public static final String RESULT_RECEIVER_ARG_NAME = "resultReceiver";
    public static final String CITY_MAP_ARG_NAME = "cityList";
    public static final String LAST_SELECTED_CITY_ARG_NAME = "lastSelectedCity";
    private static final String LONGITUDE_PROP_NAME = "longitude";
    private static final String LATITUDE_PROP_NAME = "latitude";
    private static final String TAG = WeatherApplication.class.getSimpleName();
    private static final String PROPERTIES_FILE_NAME = "app.properties";
    private static final long CACHE_SIZE = 10 * 1024 * 1024; // 10 MB;
    private ClientLocation lastClientLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        installCache();
        Properties properties = readProperties();
        lastClientLocation = readLastClientLocation(properties);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        saveProperties();
        flushCache();
    }


    public void setLastClientLocation(ClientLocation lastClientLocation) {
        this.lastClientLocation = lastClientLocation;
    }

    public ClientLocation getLastClientLocation() {
        if (lastClientLocation == null) {
            Properties properties = readProperties();
            lastClientLocation = readLastClientLocation(properties);
        }
        return lastClientLocation;
    }

    public ClientLocation readLastClientLocation(Properties properties) {
        try {
            String lastSelectedCity = properties.getProperty(LAST_SELECTED_CITY_ARG_NAME);
            String latitude = properties.getProperty(LATITUDE_PROP_NAME);
            String longitude = properties.getProperty(LONGITUDE_PROP_NAME);
            Double lastSelectedLatitude = null;
            Double lastSelectedLongitude = null;
            if (latitude != null) lastSelectedLatitude = Double.valueOf(latitude);
            if (longitude != null) lastSelectedLongitude = Double.valueOf(longitude);
            return new ClientLocation(lastSelectedLatitude, lastSelectedCity, lastSelectedLongitude);
        } catch (NumberFormatException e) {
            throw new ReadingConfigurationException("Error when parsing latitude and longitude: " + e, e);
        }
    }

    public void saveProperties() {
        //save properties file with last selected city name
        if (lastClientLocation != null) {
            Properties properties = readProperties();
            if (lastClientLocation.getCityName() != null) {
                properties.setProperty(LAST_SELECTED_CITY_ARG_NAME, lastClientLocation.getCityName());
            }
            if (lastClientLocation.getLatitude() != null && lastClientLocation.getLongitude() != null) {
                properties.setProperty(LATITUDE_PROP_NAME, String.valueOf(lastClientLocation.getLatitude()));
                properties.setProperty(LONGITUDE_PROP_NAME, String.valueOf(lastClientLocation.getLongitude()));
            }
            File file = new File(PROPERTIES_FILE_NAME);
            try {
                FileWriter writer = new FileWriter(file);
                properties.store(writer, null);
            } catch (IOException e) {
                Log.e(TAG, "Error saving properties file", e);
            }
        }
    }

    private Properties readProperties() {
        Properties properties = new Properties();
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open(PROPERTIES_FILE_NAME);
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            String detailMessage = "Error when reading properties file: " + PROPERTIES_FILE_NAME;
            Log.e(TAG, detailMessage, e);
            throw new ReadingConfigurationException(detailMessage, e);
        }
        return properties;
    }

    private void installCache() {
        try {
            File httpCacheDir = new File(getCacheDir(), "http");
            long httpCacheSize = CACHE_SIZE;
            //reflection is used to do not impact earlier android releases older than 4.0
            Class.forName("android.net.http.HttpResponseCache")
                    .getMethod("install", File.class, long.class)
                    .invoke(null, httpCacheDir, httpCacheSize);

        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
            Log.e(TAG, "HTTP response cache installation failed:", e);
        }
    }

    private void flushCache() {
        //reflection is used to do not impact earlier android releases older than 4.0
        try {
            Class<?> httpResponseCacheClass = Class.forName("android.net.http.HttpResponseCache");
            Object cache = httpResponseCacheClass.getMethod("getInstalled").invoke(null);
            if (cache != null) {
                httpResponseCacheClass.getMethod("flush").invoke(cache);
            }
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
            Log.e(TAG, "HTTP response cache flush failed:", e);
        }
    }

    public boolean hasInternet() {
        try {
            return new CheckInternetConnectionTask().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Error during checking internet connection in async task "
                    + CheckInternetConnectionTask.class.getName() + ": " + e, e);
        }
        return false;
    }

    private class CheckInternetConnectionTask extends AsyncTask<Void, Void, Boolean> {
        private static final String URL_TO_CHECK_INTERNET = "http://www.google.com";
        private static final int CONNECTION_TIMEOUT = 3 * 1000; ///3 seconds

        @Override
        protected Boolean doInBackground(Void... voids) {
            //check network first
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo == null || !netInfo.isConnectedOrConnecting()) return false;
            URL url = null;
            try {
                url = new URL(URL_TO_CHECK_INTERNET);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.getResponseCode();
            } catch (IOException e) {
                Log.e(TAG, "Exception when sending an http request with url: " + url + ", caused by:" + e, e);
                return false;
            }
            return true;
        }
    }
}

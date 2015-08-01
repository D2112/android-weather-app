package com.d2112.weather.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.*;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;
import com.d2112.weather.Files;
import com.d2112.weather.R;
import com.d2112.weather.WeatherApplication;
import com.d2112.weather.model.ClientLocation;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class LocationIntentService extends IntentService implements LocationListener, LocationService {
    public static final int GET_CURRENT_LOCATION_AND_CITY_NAME = 1;
    public static final int GET_CITY_LIST = 2;
    private static final String TAG = LocationIntentService.class.getSimpleName();
    private static final int MAX_NUMBER_OF_ADDRESSES = 1;
    private static Map<String, List<String>> citiesByCountryMap; //static is for lazy parsing
    private static String lastRememberedCurrentCityName = null; //for the case if can't get current city
    private final IBinder binder = new LocalBinder();
    private boolean isGPSProviderEnabled;
    private boolean isNetworkProviderEnabled;
    private Gson gson = new Gson();

    public LocationIntentService() {
        super("Location Service");
    }

    @Override
    public boolean isGpsAvailable() {
        refreshProviderStatus();
        return isNetworkProviderEnabled || isGPSProviderEnabled;
    }

    public String getCurrentCityName(Location location) {
        return getCurrentCityName(getCurrentLocation(), Locale.getDefault());
    }

    public String getCurrentCityName(Location currentLocation, Locale locale) {
        Geocoder geocoder = new Geocoder(getBaseContext(), locale);
        List<Address> addresses = null;
        if (currentLocation != null) {
            try {
                addresses = geocoder.getFromLocation
                        (currentLocation.getLatitude(), currentLocation.getLongitude(), MAX_NUMBER_OF_ADDRESSES);
            } catch (IOException e) {
                Log.e(TAG, "Error when getting city name from location: " + e, e);
                return lastRememberedCurrentCityName;
            }
        }
        if (addresses == null || addresses.size() == 0) return null;
        return addresses.get(0).getLocality();
    }

    public Location getCurrentLocation() {
        refreshProviderStatus();
        String provider = null;
        if (isNetworkProviderEnabled) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else if (isGPSProviderEnabled) {
            provider = LocationManager.GPS_PROVIDER;
        }
        if (provider == null) return null;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(provider, 0, 0, this);
        return locationManager.getLastKnownLocation(provider);
    }

    public Map<String, List<String>> getCitiesByCountryMap() {
        if (citiesByCountryMap == null) {
            citiesByCountryMap = parseCitiesFile();
        }
        return citiesByCountryMap;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = new Bundle();
        int action = intent.getFlags();
        ResultReceiver resultReceiver = intent.getParcelableExtra(WeatherApplication.RESULT_RECEIVER_ARG_NAME);
        if (action == GET_CURRENT_LOCATION_AND_CITY_NAME) {
            Location currentLocation = getCurrentLocation();
            String currentCityName = getCurrentCityName(currentLocation);
            if (currentLocation != null && currentCityName != null) {
                ClientLocation clientLocation = new ClientLocation(
                        currentLocation.getLatitude(),
                        currentCityName,
                        currentLocation.getLongitude()
                );
                bundle.putParcelable(WeatherApplication.CLIENT_LOCATION_ARG_NAME, clientLocation);
                resultReceiver.send(Activity.RESULT_OK, bundle);
            } else {
                resultReceiver.send(Activity.RESULT_CANCELED, null);
            }
        }
        if (action == GET_CITY_LIST) {
            Map<String, List<String>> citiesByCountryMap = getCitiesByCountryMap();
            bundle.putSerializable(WeatherApplication.CITY_MAP_ARG_NAME, new TreeMap<>(citiesByCountryMap));
            resultReceiver.send(Activity.RESULT_OK, bundle);
        }
    }

    private void refreshProviderStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isGPSProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private Map<String, List<String>> parseCitiesFile() {
        Map<String, List<String>> citiesByCountryMap = new TreeMap<>();
        List<String> cityList;
        //read json string from the file
        InputStream in = getResources().openRawResource(R.raw.cities);
        String jsonCountriesAndCities = Files.convertStreamToString(in);
        try {
            in.close();
        } catch (IOException e) {
            Log.e(TAG, "Error when reading cities file: " + e, e);
        }
        //parse json string with cities
        JsonObject jsonObject = gson.fromJson(jsonCountriesAndCities, JsonObject.class);
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            cityList = new ArrayList<>();
            String countryName = entry.getKey();
            //get value as a json array and add all its item to city list
            for (JsonElement cityJsonElement : entry.getValue().getAsJsonArray()) {
                String cityName = cityJsonElement.getAsString();
                cityList.add(cityName);
            }
            citiesByCountryMap.put(countryName, cityList);
        }
        return citiesByCountryMap;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    public class LocalBinder extends Binder {
        public LocationService getService() {
            // Return this instance of service so clients can call public methods
            return LocationIntentService.this;
        }
    }

}
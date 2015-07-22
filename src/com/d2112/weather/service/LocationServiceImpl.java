package com.d2112.weather.service;

import com.d2112.weather.Files;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LocationServiceImpl implements LocationService {
    private static final String CITIES_FILE_PATH = "raw/cities.json";
    private Gson gson = new Gson();

    @Override
    public Map<String, List<String>> getCitiesByCountryMap() {
        Map<String, List<String>> citiesByCountryMap = new TreeMap<>();
        List<String> cityList;

        //read string from file
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(CITIES_FILE_PATH);
        String jsonCountriesAndCities = Files.convertStreamToString(in);

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
}

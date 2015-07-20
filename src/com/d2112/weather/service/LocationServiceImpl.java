package com.d2112.weather.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationServiceImpl implements LocationService {

    @Override
    public Map<String, List<String>> getCitiesByCountryMap() {
        Map<String, List<String>> citiesByCountryMap = new HashMap<>();
        List<String> cities = new ArrayList<>();
        cities.add("Tokyo");
        cities.add("Mokyo");
        cities.add("Rokyo");
        citiesByCountryMap.put("Japan", cities);

        cities = new ArrayList<>();
        cities.add("Zokyo");
        cities.add("Vokyo");
        cities.add("Dokyo");
        cities.add("Bokyo");
        cities.add("Kokyo");
        citiesByCountryMap.put("Hapan", cities);

        cities = new ArrayList<>();
        cities.add("Uokyo");
        cities.add("Iokyo");
        cities.add("Ookyo");
        cities.add("Pokyo");
        cities.add("Qokyo");
        citiesByCountryMap.put("Qapan", cities);

        return citiesByCountryMap;
    }
}

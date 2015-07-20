package com.d2112.weather.service;

import java.util.List;
import java.util.Map;

public interface LocationService {
    Map<String,List<String>> getCitiesByCountryMap();
}

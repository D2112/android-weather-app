package com.d2112.weather;

public class TemperatureFormatter {

    static public String formatCelsius(double celsius) {
        int roundedCelsius = (int) celsius;
        return roundedCelsius > 0 ? ("+" + roundedCelsius) : String.valueOf(roundedCelsius);
    }
}

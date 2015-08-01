package com.d2112.weather;

public class WeatherValuesFormatter {
    private static final String WIND_SPEED_UNIT_POSTFIX = "m/s";


    static public String formatWindDegree(double windDegree) {
        return DIRECTIONS[(int) Math.round((((int) windDegree % 360) / 45))];
    }

    static public String formatWindSpeed(double windSpeed) {
        return windSpeed + " " + WIND_SPEED_UNIT_POSTFIX;
    }

    static public String formatCelsius(double celsius) {
        int roundedCelsius = (int) celsius;
        //if more than zero, then add plus
        return roundedCelsius > 0 ? ("+" + roundedCelsius) : String.valueOf(roundedCelsius);
    }

    static public String formatHumidity(int humidity) {
        return humidity + "%";
    }

    private static final String DIRECTIONS[] =
            {
                    "North",
                    "North-East",
                    "East",
                    "South-East",
                    "South",
                    "South-West",
                    "West",
                    "North-West"
            };
}

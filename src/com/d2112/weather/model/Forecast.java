package com.d2112.weather.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Forecast implements Parcelable {
    private Map<TimeOfDay, Temperature> temperatureByTimeOfDay;
    private Date date;
    private Wind wind;
    private int humidity; //0-100%
    private String iconCode;
    private String cityName;
    private String description;

    private Forecast() {
        temperatureByTimeOfDay = new HashMap<>();
    }

    public Date getDate() {
        return date;
    }

    public Wind getWind() {
        return wind;
    }

    public int getHumidity() {
        return humidity;
    }

    public String getIconCode() {
        return iconCode;
    }

    public String getDescription() {
        return description;
    }

    public String getCityName() {
        return cityName;
    }

    public Temperature getTemperature(TimeOfDay timeOfDay) {
        return temperatureByTimeOfDay.get(timeOfDay);
    }

    public enum TimeOfDay {
        MORNING, DAY, EVENING, NIGHT
    }

    /*----BUILDER----*/

    public static Builder newBuilder() {
        return new Forecast().new Builder();
    }

    public class Builder {

        private Builder() {
        }

        public Builder setDate(Date date) {
            Forecast.this.date = date;
            return this;
        }

        public Builder setHumidity(int humidity) {
            Forecast.this.humidity = humidity;
            return this;
        }

        public Builder setCityName(String cityName) {
            Forecast.this.cityName = cityName;
            return this;
        }

        public Builder setDescription(String description) {
            Forecast.this.description = description;
            return this;
        }

        public Builder setWind(Wind wind) {
            Forecast.this.wind = wind;
            return this;
        }

        public Builder setIconCode(String iconCode) {
            Forecast.this.iconCode = iconCode;
            return this;
        }

        public Builder setTemperature(TimeOfDay timeOfDay, Temperature temperature) {
            temperatureByTimeOfDay.put(timeOfDay, temperature);
            return this;
        }

        public Forecast build() {
            return Forecast.this;
        }
    }

    /*----PARCELABLE STUFF----*/
    private static final String MAP_SIZE_ATTRIBUTE_NAME = "size";
    private static final String MAP_KEY_ATTRIBUTE_NAME = "key";
    private static final String MAP_VALUE_ATTRIBUTE_NAME = "value";

    private Forecast(Parcel in) {
        date = new Date(in.readLong());
        humidity = in.readInt();
        iconCode = in.readString();
        cityName = in.readString();
        description = in.readString();
        wind = in.readParcelable(Wind.class.getClassLoader());

        //reading map from bundle
        Bundle bundle = in.readBundle();
        int mapSize = bundle.getInt(MAP_SIZE_ATTRIBUTE_NAME);
        String mapKey = bundle.getString(MAP_KEY_ATTRIBUTE_NAME);
        Temperature mapValue = bundle.getParcelable(MAP_VALUE_ATTRIBUTE_NAME);
        temperatureByTimeOfDay = new HashMap<>();
        for (int i = 0; i < mapSize; i++) {
            temperatureByTimeOfDay.put(TimeOfDay.valueOf(mapKey), mapValue);
        }
    }

    @Override
    public void writeToParcel(Parcel out, int parcelableFlags) {
        out.writeLong(date.getTime());
        out.writeInt(humidity);
        out.writeString(iconCode);
        out.writeString(cityName);
        out.writeString(description);
        out.writeParcelable(wind, parcelableFlags);

        //write map values to bundle and write bundle to parcel
        Bundle bundle = new Bundle();
        bundle.putInt(MAP_SIZE_ATTRIBUTE_NAME, temperatureByTimeOfDay.size());
        for (Map.Entry<TimeOfDay, Temperature> entry : temperatureByTimeOfDay.entrySet()) {
            bundle.putString(MAP_KEY_ATTRIBUTE_NAME, entry.getKey().name());
            bundle.putParcelable(MAP_VALUE_ATTRIBUTE_NAME, entry.getValue());
        }
        out.writeBundle(bundle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Forecast> CREATOR = new Parcelable.Creator<Forecast>() {

        @Override
        public Forecast createFromParcel(Parcel parcel) {
            return new Forecast(parcel);
        }

        @Override
        public Forecast[] newArray(int size) {
            return new Forecast[size];
        }
    };
}

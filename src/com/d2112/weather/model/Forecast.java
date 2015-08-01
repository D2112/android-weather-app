package com.d2112.weather.model;

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

    @Override
    public String toString() {
        return "Forecast{" +
                "temperatureByTimeOfDay=" + temperatureByTimeOfDay +
                ", date=" + date +
                ", wind=" + wind +
                ", humidity=" + humidity +
                ", iconCode='" + iconCode + '\'' +
                ", cityName='" + cityName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Forecast forecast = (Forecast) o;

        if (humidity != forecast.humidity) return false;
        if (temperatureByTimeOfDay != null ? !temperatureByTimeOfDay.equals(forecast.temperatureByTimeOfDay) : forecast.temperatureByTimeOfDay != null)
            return false;
        if (date != null ? !date.equals(forecast.date) : forecast.date != null) return false;
        if (wind != null ? !wind.equals(forecast.wind) : forecast.wind != null) return false;
        if (iconCode != null ? !iconCode.equals(forecast.iconCode) : forecast.iconCode != null) return false;
        if (cityName != null ? !cityName.equals(forecast.cityName) : forecast.cityName != null) return false;
        return !(description != null ? !description.equals(forecast.description) : forecast.description != null);

    }

    @Override
    public int hashCode() {
        int result = temperatureByTimeOfDay != null ? temperatureByTimeOfDay.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (wind != null ? wind.hashCode() : 0);
        result = 31 * result + humidity;
        result = 31 * result + (iconCode != null ? iconCode.hashCode() : 0);
        result = 31 * result + (cityName != null ? cityName.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
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

        //filling map
        temperatureByTimeOfDay = new HashMap<>();
        for (TimeOfDay mapKey : TimeOfDay.values()) {
            Temperature mapValue = in.readParcelable(Temperature.class.getClassLoader());
            temperatureByTimeOfDay.put(mapKey, mapValue);
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

        for (TimeOfDay timeOfDay : TimeOfDay.values()) {
            out.writeParcelable(temperatureByTimeOfDay.get(timeOfDay), parcelableFlags);
        }
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

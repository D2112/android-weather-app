package com.d2112.weather.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ClientLocation implements Parcelable {
    private String cityName;
    private Double latitude;
    private Double longitude;

    public ClientLocation(Double latitude, String cityName, Double longitude) {
        this.cityName = cityName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCityName() {
        return cityName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "ClientLocation{" +
                "cityName='" + cityName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientLocation that = (ClientLocation) o;

        if (cityName != null ? !cityName.equals(that.cityName) : that.cityName != null) return false;
        if (latitude != null ? !latitude.equals(that.latitude) : that.latitude != null) return false;
        return !(longitude != null ? !longitude.equals(that.longitude) : that.longitude != null);
    }

    @Override
    public int hashCode() {
        int result = cityName != null ? cityName.hashCode() : 0;
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        return result;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(cityName);
        parcel.writeValue(latitude);
        parcel.writeValue(longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<ClientLocation> CREATOR = new Parcelable.Creator<ClientLocation>() {

        @Override
        public ClientLocation createFromParcel(Parcel parcel) {
            String cityName = parcel.readString();
            Double latitude = (Double) parcel.readValue(Double.class.getClassLoader());
            Double longitude = (Double) parcel.readValue(Double.class.getClassLoader());
            return new ClientLocation(latitude, cityName, longitude);
        }

        @Override
        public ClientLocation[] newArray(int size) {
            return new ClientLocation[size];
        }
    };
}

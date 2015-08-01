package com.d2112.weather.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Temperature implements Parcelable {
    private double celsius;

    public Temperature(double celsius) {
        this.celsius = celsius;
    }

    public double getAsCelsius() {
        return celsius;
    }

    @Override
    public String toString() {
        return "Temperature{" +
                "celsius=" + celsius +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Temperature that = (Temperature) o;

        return Double.compare(that.celsius, celsius) == 0;

    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(celsius);
        return (int) (temp ^ (temp >>> 32));
    }

    /*----Parcelable stuff----*/

    private Temperature(Parcel in) {
        celsius = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int parcelableFlags) {
        parcel.writeDouble(celsius);
    }

    public static final Parcelable.Creator<Temperature> CREATOR = new Parcelable.Creator<Temperature>() {

        @Override
        public Temperature createFromParcel(Parcel parcel) {
            return new Temperature(parcel);
        }

        @Override
        public Temperature[] newArray(int size) {
            return new Temperature[size];
        }
    };
}

package com.d2112.weather.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Temperature implements Parcelable {
    private double celsius;
    private double kelvin;

    public Temperature(double celsius) {
        this.celsius = celsius;
    }

    private Temperature(Parcel in) {
        celsius = in.readInt();
    }

    public double getAsCelsius() {
        return celsius;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
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

package com.d2112.weather.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Wind implements Parcelable {
    private double windSpeed;
    private int windDegree;

    public Wind(double windSpeed, int windDegree) {
        this.windSpeed = windSpeed;
        this.windDegree = windDegree;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public int getWindDegree() {
        return windDegree;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(windSpeed);
        parcel.writeInt(windDegree);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Wind> CREATOR = new Parcelable.Creator<Wind>() {

        @Override
        public Wind createFromParcel(Parcel parcel) {
            int speed = parcel.readInt();
            double degree = parcel.readDouble();
            return new Wind(degree, speed);
        }

        @Override
        public Wind[] newArray(int size) {
            return new Wind[size];
        }
    };
}

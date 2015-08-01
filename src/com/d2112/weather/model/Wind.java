package com.d2112.weather.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Wind implements Parcelable {
    private double speed;
    private int degree;

    public Wind(double speed, int degree) {
        this.speed = speed;
        this.degree = degree;
    }

    public double getSpeed() {
        return speed;
    }

    public int getDegree() {
        return degree;
    }

    @Override
    public String toString() {
        return "Wind{" +
                "speed=" + speed +
                ", degree=" + degree +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Wind wind = (Wind) o;

        return Double.compare(wind.speed, speed) == 0 && degree == wind.degree;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(speed);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + degree;
        return result;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(speed);
        parcel.writeInt(degree);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Wind> CREATOR = new Parcelable.Creator<Wind>() {

        @Override
        public Wind createFromParcel(Parcel parcel) {
            double speed = parcel.readDouble();
            int degree = parcel.readInt();
            return new Wind(speed, degree);
        }

        @Override
        public Wind[] newArray(int size) {
            return new Wind[size];
        }
    };
}

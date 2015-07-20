package com.d2112.weather.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Forecast implements Parcelable {
    private Date date;
    private List<Temperature> temperatures;

    public Forecast(Date date, List<Temperature> temperatures) {
        this.date = date;
    }

    private Forecast(Parcel in) {
        date = new Date(in.readLong());
        temperatures = new ArrayList<>();
        temperatures = in.createTypedArrayList(Temperature.CREATOR);
    }

    public Date getDate() {
        return date;
    }

    public Temperature getMaxTemperature() {
        return new Temperature(99);
    }

    public Temperature getMinTemperature() {
        return new Temperature(-99);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        for (Temperature temperature : temperatures) {
            parcel.writeParcelable(temperature, i);
        }
        parcel.writeLong(date.getTime());
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

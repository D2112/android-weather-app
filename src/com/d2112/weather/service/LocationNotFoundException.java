package com.d2112.weather.service;

public class LocationNotFoundException extends Exception {
    public LocationNotFoundException() {
    }

    public LocationNotFoundException(String detailMessage) {
        super(detailMessage);
    }

    public LocationNotFoundException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public LocationNotFoundException(Throwable throwable) {
        super(throwable);
    }
}

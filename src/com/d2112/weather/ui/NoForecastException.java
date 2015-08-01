package com.d2112.weather.ui;

public class NoForecastException extends RuntimeException {
    public NoForecastException() {
    }

    public NoForecastException(String detailMessage) {
        super(detailMessage);
    }

    public NoForecastException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NoForecastException(Throwable throwable) {
        super(throwable);
    }
}

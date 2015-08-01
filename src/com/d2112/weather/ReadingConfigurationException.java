package com.d2112.weather;

public class ReadingConfigurationException extends RuntimeException {

    public ReadingConfigurationException() {
    }

    public ReadingConfigurationException(String detailMessage) {
        super(detailMessage);
    }

    public ReadingConfigurationException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ReadingConfigurationException(Throwable throwable) {
        super(throwable);
    }
}

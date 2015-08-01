package com.d2112.weather;

import java.io.InputStream;

public class Files {
    private static final String DEFAULT_CHARSET = "UTF-8";

    static public String convertStreamToString(InputStream is) {
        return convertStreamToString(is, DEFAULT_CHARSET);
    }

    static public String convertStreamToString(InputStream is, String charsetName) {
        java.util.Scanner scanner = new java.util.Scanner(is, charsetName).useDelimiter("\\A"); // The "A" token means beginning of the input boundary
        return scanner.hasNext() ? scanner.next() : ""; //if input isn't empty then return its content as a string
    }
}

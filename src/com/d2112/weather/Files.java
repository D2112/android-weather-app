package com.d2112.weather;

import java.io.InputStream;

public class Files {

    static public String convertStreamToString(InputStream is) {
        return convertStreamToString(is, "UTF-8");
    }

    static public String convertStreamToString(InputStream is, String charsetName) {
        java.util.Scanner s = new java.util.Scanner(is, charsetName).useDelimiter("\\A"); // The "A" token means beginning of the input boundary
        return s.hasNext() ? s.next() : ""; //if input isn't empty then return its content as a string
    }
}

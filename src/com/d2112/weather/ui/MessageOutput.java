package com.d2112.weather.ui;

import android.graphics.Color;
import android.widget.TextView;

public class MessageOutput {
    private static final int ERROR_TEXT_COLOR = Color.RED;
    private static final int NORMAL_TEXT_COLOR = Color.WHITE;
    private static final int SUCCESS_TEXT_COLOR = Color.GREEN;
    private TextView textView;

    public MessageOutput(TextView textView) {
        this.textView = textView;
    }


    public void clearText() {
        textView.setText("");
    }

    public void setErrorText(int resId) {
        textView.setTextColor(ERROR_TEXT_COLOR);
        textView.setText(resId);
    }

    public void setText(int resId) {
        textView.setTextColor(NORMAL_TEXT_COLOR);
        textView.setText(resId);
    }

    public void setSuccessText(int resId) {
        textView.setTextColor(SUCCESS_TEXT_COLOR);
        textView.setText(resId);
    }

    public void setErrorText(String text) {
        textView.setTextColor(ERROR_TEXT_COLOR);
        textView.setText(text);
    }

    public void setText(String text) {
        textView.setTextColor(NORMAL_TEXT_COLOR);
        textView.setText(text);
    }

    public void setSuccessText(String text) {
        textView.setTextColor(SUCCESS_TEXT_COLOR);
        textView.setText(text);
    }
}

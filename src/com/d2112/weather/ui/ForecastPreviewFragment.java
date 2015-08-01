package com.d2112.weather.ui;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.d2112.weather.R;
import com.d2112.weather.WeatherApplication;
import com.d2112.weather.WeatherValuesFormatter;
import com.d2112.weather.model.Forecast;
import com.d2112.weather.model.Temperature;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ForecastPreviewFragment extends Fragment {
    private static final String DATE_PATTERN = "EEE dd/MM";
    private View view;
    private Forecast forecast;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.forecast_preview_fragment, null);
        view.setFocusable(true);
        forecast = getArguments().getParcelable(WeatherApplication.FORECAST_ARG_NAME);
        setForecastValues();
        setListeners();
        return view;
    }

    private void setListeners() {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.requestFocus();
            }
        });

        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    ForecastActivity forecastActivity = (ForecastActivity) getActivity();
                    forecastActivity.setActivityValues(forecast);
                }
            }
        });
    }

    private void setForecastValues() {
        TextView date = (TextView) view.findViewById(R.id.previewDate);
        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        TextView dayTemperatureView = (TextView) view.findViewById(R.id.dayTemperature);
        TextView nightTemperatureView = (TextView) view.findViewById(R.id.nightTemperature);
        Temperature dayTemperature = forecast.getTemperature(Forecast.TimeOfDay.DAY);
        Temperature nightTemperature = forecast.getTemperature(Forecast.TimeOfDay.NIGHT);

        IconManager iconManager = new IconManager(getActivity().getApplication());
        Drawable weatherIcon = iconManager.getIconByForecastCode(forecast.getIconCode());

        iconView.setImageDrawable(weatherIcon);
        dayTemperatureView.setText(WeatherValuesFormatter.formatCelsius(dayTemperature.getAsCelsius()));
        nightTemperatureView.setText(WeatherValuesFormatter.formatCelsius(nightTemperature.getAsCelsius()));
        date.setText(formatDate(forecast.getDate()));
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat(DATE_PATTERN).format(date);
    }
}

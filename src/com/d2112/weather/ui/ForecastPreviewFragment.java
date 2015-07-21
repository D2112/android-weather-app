package com.d2112.weather.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.d2112.weather.R;
import com.d2112.weather.model.Forecast;

import java.text.DateFormat;

public class ForecastPreviewFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.forecast_preview_fragment, null);
        Forecast forecast = getArguments().getParcelable("forecast");

        TextView date = (TextView) view.findViewById(R.id.date);
        TextView maxTemperatureView = (TextView) view.findViewById(R.id.MAX_TEMPERATURE);
        TextView minTemperatureView = (TextView) view.findViewById(R.id.MIN_TEMPERATURE);
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity().getApplicationContext());
        date.setText(dateFormat.format(forecast.getDate()));
/*        Temperature maxTemperature = forecast.getMaxTemperature();
        Temperature minTemperature = forecast.getMinTemperature();
        maxTemperatureView.setText(String.valueOf(maxTemperature.getAsCelsius()));
        minTemperatureView.setText(String.valueOf(minTemperature.getAsCelsius()));*/
        return view;
    }
}

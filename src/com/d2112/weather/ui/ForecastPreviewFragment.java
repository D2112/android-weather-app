package com.d2112.weather.ui;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.d2112.weather.R;
import com.d2112.weather.TemperatureFormatter;
import com.d2112.weather.model.Forecast;
import com.d2112.weather.model.Temperature;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ForecastPreviewFragment extends Fragment {
    private static final String DATE_PATTERN = "EEE dd/MM";
    private static final String ICON_IMAGE_PREFIX = "icon";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.forecast_preview_fragment, null);
        Forecast forecast = getArguments().getParcelable("forecast");

        TextView date = (TextView) view.findViewById(R.id.date);
        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        TextView dayTemperatureView = (TextView) view.findViewById(R.id.dayTemperature);
        TextView nightTemperatureView = (TextView) view.findViewById(R.id.nightTemperature);
        Temperature dayTemperature = forecast.getTemperature(Forecast.TimeOfDay.DAY);
        Temperature nightTemperature = forecast.getTemperature(Forecast.TimeOfDay.NIGHT);

        dayTemperatureView.setText(TemperatureFormatter.formatCelsius(dayTemperature.getAsCelsius()));
        nightTemperatureView.setText(TemperatureFormatter.formatCelsius(nightTemperature.getAsCelsius()));
        date.setText(formatDate(forecast.getDate()));
        iconView.setImageDrawable(getDrawableByForecastCode(forecast.getIconCode()));
        return view;
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        return sdf.format(date);
    }

    private Drawable getDrawableByForecastCode(String forecastCode) {
        Resources resources = getResources();
        String imageName = ICON_IMAGE_PREFIX + forecastCode;
        final int resourceId = resources.getIdentifier(imageName, "drawable", getActivity().getPackageName());
        return resources.getDrawable(resourceId, null);
    }
}

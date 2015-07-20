package com.d2112.weather.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import com.d2112.weather.R;
import com.d2112.weather.model.Forecast;
import com.d2112.weather.service.WeatherService;
import com.d2112.weather.service.WeatherServiceImpl;

import java.util.List;

public class ForecastActivity extends Activity {
    private WeatherService weatherService;

    public ForecastActivity() {
        weatherService = new WeatherServiceImpl();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forecast);

        Intent intent = getIntent();
        String cityName = intent.getStringExtra("cityName");

        List<Forecast> forecastList = weatherService.getForecastForFewWeeks();

        //render forecast preview fragments within a transaction
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        for (Forecast forecast : forecastList) {
            Fragment forecastPreview = new ForecastPreviewFragment();
            //pass forecast as argument into forecast preview fragment
            Bundle bundle = new Bundle();
            bundle.putParcelable("forecast", forecast);
            forecastPreview.setArguments(bundle);
            ft.add(R.id.forecastPreviewContainer, forecastPreview);
        }
        ft.commit();
    }
}

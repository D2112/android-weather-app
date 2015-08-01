package com.d2112.weather.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.d2112.weather.R;
import com.d2112.weather.WeatherApplication;
import com.d2112.weather.WeatherValuesFormatter;
import com.d2112.weather.model.Forecast;
import com.d2112.weather.model.Temperature;
import com.d2112.weather.service.WeatherService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ForecastActivity extends Activity {
    private static final String TAG = ForecastActivity.class.getSimpleName();
    private static final String DATE_PATTERN = "EEE dd/MM/yyyy";
    static final int GET_LOCATION_REQUEST = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forecast);

        sendCallForWeekForecast(getIntent());

        //return on selecting city activity when click on change city button
        View changeCityButton = findViewById(R.id.changeCityButton);
        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSelectingCityActivityToGetLocation();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_LOCATION_REQUEST) {
            if (resultCode == RESULT_OK) {
                sendCallForWeekForecast(data);
            }
        }
    }

    void setActivityValues(Forecast forecast) {
        ImageView weatherIcon = (ImageView) findViewById(R.id.weatherIcon);
        TextView date = (TextView) findViewById(R.id.date);
        TextView weatherDescription = (TextView) findViewById(R.id.weatherDescription);
        TextView cityNameValue = (TextView) findViewById(R.id.cityNameValue);
        TextView morningValue = (TextView) findViewById(R.id.morningValue);
        TextView dayValue = (TextView) findViewById(R.id.dayValue);
        TextView eveningValue = (TextView) findViewById(R.id.eveningValue);
        TextView nightValue = (TextView) findViewById(R.id.nightValue);
        TextView windSpeedValue = (TextView) findViewById(R.id.windSpeedValue);
        TextView windDegreeValue = (TextView) findViewById(R.id.windDegreeValue);
        TextView humidityValue = (TextView) findViewById(R.id.humidityValue);

        Temperature morningTemp = forecast.getTemperature(Forecast.TimeOfDay.MORNING);
        Temperature dayTemp = forecast.getTemperature(Forecast.TimeOfDay.DAY);
        Temperature eveningTemp = forecast.getTemperature(Forecast.TimeOfDay.EVENING);
        Temperature nightTemp = forecast.getTemperature(Forecast.TimeOfDay.NIGHT);

        IconManager iconManager = new IconManager(getApplicationContext());
        weatherIcon.setImageDrawable(iconManager.getIconByForecastCode(forecast.getIconCode()));
        weatherDescription.setText(forecast.getDescription());
        cityNameValue.setText(forecast.getCityName());
        morningValue.setText(WeatherValuesFormatter.formatCelsius(morningTemp.getAsCelsius()));
        dayValue.setText(WeatherValuesFormatter.formatCelsius(dayTemp.getAsCelsius()));
        eveningValue.setText(WeatherValuesFormatter.formatCelsius(eveningTemp.getAsCelsius()));
        nightValue.setText(WeatherValuesFormatter.formatCelsius(nightTemp.getAsCelsius()));
        windSpeedValue.setText(WeatherValuesFormatter.formatWindSpeed(forecast.getWind().getSpeed()));
        windDegreeValue.setText(WeatherValuesFormatter.formatWindDegree(forecast.getWind().getDegree()));
        humidityValue.setText(WeatherValuesFormatter.formatHumidity(forecast.getHumidity()));
        date.setText(new SimpleDateFormat(DATE_PATTERN).format(forecast.getDate()));
    }

    private void startSelectingCityActivityToGetLocation() {
        finish(); //close current activity
        Intent intent = new Intent(ForecastActivity.this, SelectingCityActivity.class);
        startActivityForResult(intent, GET_LOCATION_REQUEST);
    }

    private void sendCallForWeekForecast(Intent dataForService) {
        dataForService.setClass(getBaseContext(), WeatherService.class);
        dataForService.setFlags(WeatherService.GET_WEEK_FORECAST_ACTION);
        dataForService.putExtra(WeatherApplication.RESULT_RECEIVER_ARG_NAME, new ForecastReceiver(new Handler()));
        startService(dataForService);
    }

    private void renderForecastPreviewMenu(List<Forecast> forecastList) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        for (Forecast forecast : forecastList) {
            Fragment forecastPreview = new ForecastPreviewFragment();
            //pass forecast as argument into forecast preview fragment
            Bundle bundle = new Bundle();
            bundle.putParcelable(WeatherApplication.FORECAST_ARG_NAME, forecast);
            forecastPreview.setArguments(bundle);
            ft.add(R.id.forecastPreviewContainer, forecastPreview);
        }
        ft.commit();
    }

    private class ForecastReceiver extends ResultReceiver {

        public ForecastReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == RESULT_OK) {
                ArrayList<Forecast> forecastList = resultData.getParcelableArrayList(WeatherApplication.FORECAST_LIST_ARG_NAME);
                if (forecastList == null || forecastList.size() == 0) {
                    String detailMessage = "The forecast list in forecast activity is empty";
                    Log.e(TAG, detailMessage);
                    throw new NoForecastException(detailMessage);
                }
                renderForecastPreviewMenu(forecastList);
                setActivityValues(forecastList.get(0)); //forecast for today
            }
            if (resultCode == RESULT_CANCELED) {
                startActivity(new Intent(ForecastActivity.this, SelectingCityActivity.class)); //returning to main activity
            }
        }
    }
}

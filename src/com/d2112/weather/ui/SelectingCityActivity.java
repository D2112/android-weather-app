package com.d2112.weather.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import com.d2112.weather.R;
import com.d2112.weather.WeatherApplication;
import com.d2112.weather.model.ClientLocation;
import com.d2112.weather.service.LocationIntentService;
import com.d2112.weather.service.LocationService;

import java.util.List;
import java.util.Map;

public class SelectingCityActivity extends Activity {
    private WeatherApplication weatherApplication;
    private TextView cityNameInput;
    private MessageOutput messageOutput;
    private boolean locationServiceBound;
    private LocationService locationService;
    private ClientLocation clientLocationFromGps = new ClientLocation();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selecting_city);
        sendCallForCityList(); //request a city list from the location service
        weatherApplication = (WeatherApplication) getApplicationContext();

        cityNameInput = (TextView) findViewById(R.id.cityNameInput);
        messageOutput = new MessageOutput((TextView) findViewById(R.id.messageOutput));

        ClientLocation lastClientLocation = weatherApplication.getLastClientLocation();
        String lastSelectedCity = lastClientLocation.getCityName();
        if (lastSelectedCity != null) {
            cityNameInput.setText(lastSelectedCity);
        } else {
            if (isGpsAvailable()) {
                sendCallForGpsLocation();
            }
        }

        //setting OK button listener
        View okButton = findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            //check user's input and weather service availability and then get forecast and start forecast activity
            public void onClick(View view) {
                String selectedCity = cityNameInput.getText().toString();
                if (selectedCity.isEmpty()) {
                    messageOutput.setErrorText(R.string.empty_city_error);
                    return;
                }
                if (!weatherApplication.hasInternet()) {
                    messageOutput.setErrorText(R.string.no_network_error);
                    return;
                }
                startForecastActivity(selectedCity);
            }
        });

        View gpsButton = findViewById(R.id.gpsButton);
        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCallForGpsLocation();
            }
        });

        View chooseCityFromListButton = findViewById(R.id.chooseCityFromListButton);
        chooseCityFromListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNextLayout();
            }
        });
    }

    @Override
    public void onBackPressed() {
        ViewFlipper flipper = (ViewFlipper) findViewById(R.id.selecting_city_flipper);
        View currentView = flipper.getCurrentView();
        if (currentView.getId() == R.id.cityList) {
            showPreviousLayout();
        } else super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, LocationIntentService.class), connectionToLocationService, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationServiceBound) {
            unbindService(connectionToLocationService);
            locationServiceBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, LocationIntentService.class));
        weatherApplication.saveProperties();
    }

    private boolean isGpsAvailable() {
        return locationServiceBound && locationService.isGpsAvailable();
    }

    private void startForecastActivity(String selectedCity) {
        Intent intent = new Intent();
        ClientLocation selectedLocation;
        //if city name was found by gps then use location from gps, otherwise use location with city name from input
        //and without coordinates
        if (selectedCity.equalsIgnoreCase(clientLocationFromGps.getCityName())) {
            selectedLocation = clientLocationFromGps;
        } else {
            selectedLocation = new ClientLocation(null, selectedCity, null);
        }
        weatherApplication.setLastClientLocation(selectedLocation); //remember used location
        intent.putExtra(WeatherApplication.CLIENT_LOCATION_ARG_NAME, selectedLocation);

        //check whether activity was called for result or it's just first application's launch
        if (getCallingActivity() == null) {
            //start new activity because it called first time
            intent.setClass(this, ForecastActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            //return result to calling activity
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void createCityListView(Map<String, List<String>> citiesByCountryMap) {
        ExpandableListView countryListView = (ExpandableListView) findViewById(R.id.cityList);
        TextExpandableListAdapter adapter = new TextExpandableListAdapter(this, citiesByCountryMap);
        countryListView.setAdapter(adapter);
        countryListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                TextView childTextView = (TextView) v.findViewById(R.id.childTextView);
                String cityName = (String) childTextView.getText();
                cityNameInput.setText(cityName);
                if (!weatherApplication.hasInternet()) {
                    messageOutput.setErrorText(R.string.no_network_error);
                    showPreviousLayout(); // go to layout where the error was displayed
                    return false;
                }
                startForecastActivity(cityName);
                return true;
            }
        });
    }

    private void showNextLayout() {
        ViewFlipper flipper = (ViewFlipper) findViewById(R.id.selecting_city_flipper);
        flipper.setInAnimation(SelectingCityActivity.this, R.anim.slide_up_in);
        flipper.setOutAnimation(SelectingCityActivity.this, R.anim.slide_up_out);
        flipper.showNext();
    }

    private void showPreviousLayout() {
        ViewFlipper flipper = (ViewFlipper) findViewById(R.id.selecting_city_flipper);
        flipper.setInAnimation(SelectingCityActivity.this, R.anim.slide_down_in);
        flipper.setOutAnimation(SelectingCityActivity.this, R.anim.slide_down_out);
        flipper.showPrevious();
    }

    private void sendCallForGpsLocation() {
        Intent intent = new Intent(getBaseContext(), LocationIntentService.class);
        intent.setFlags(LocationIntentService.GET_CURRENT_LOCATION_AND_CITY_NAME);
        LocationReceiver locationReceiver = new LocationReceiver(new Handler());
        intent.putExtra(WeatherApplication.RESULT_RECEIVER_ARG_NAME, locationReceiver);
        messageOutput.setText(R.string.waiting_gps);
        startService(intent);
    }

    private void sendCallForCityList() {
        Intent intent = new Intent(getBaseContext(), LocationIntentService.class);
        intent.setFlags(LocationIntentService.GET_CITY_LIST);
        CityListReceiver cityListReceiver = new CityListReceiver(new Handler());
        intent.putExtra(WeatherApplication.RESULT_RECEIVER_ARG_NAME, cityListReceiver);
        startService(intent);
    }

    private class LocationReceiver extends ResultReceiver {

        public LocationReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == RESULT_OK) {
                clientLocationFromGps = resultData.getParcelable(WeatherApplication.CLIENT_LOCATION_ARG_NAME);
                String currentCityName = clientLocationFromGps.getCityName();
                if (currentCityName != null) {
                    cityNameInput.setText(currentCityName);
                    messageOutput.clearText(); //clear messages
                }
            }
            if (resultCode == RESULT_CANCELED) {
                messageOutput.setErrorText(R.string.no_gps_error);
            }
        }
    }

    private class CityListReceiver extends ResultReceiver {

        public CityListReceiver(Handler handler) {
            super(handler);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == RESULT_OK) {
                Map<String, List<String>> citiesByCountryList =
                        (Map<String, List<String>>) resultData.getSerializable(WeatherApplication.CITY_MAP_ARG_NAME);
                createCityListView(citiesByCountryList);
            }
        }

    }

    private ServiceConnection connectionToLocationService = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocationIntentService.LocalBinder binder = (LocationIntentService.LocalBinder) service;
            locationService = binder.getService();
            locationServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            locationServiceBound = false;
        }
    };

}

package com.d2112.weather.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.d2112.weather.R;
import com.d2112.weather.WeatherApplication;
import com.d2112.weather.model.ClientLocation;

/**
 * The starting application class that decides which activity should be launched next
 */
public class StartingActivity extends Activity {
    private AlertDialog noInternetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WeatherApplication weatherApplication = (WeatherApplication) getApplicationContext();

        ClientLocation lastClientLocation = weatherApplication.getLastClientLocation();
        String lastSelectedCity = lastClientLocation.getCityName();
        if (lastSelectedCity == null) {
            if (!weatherApplication.hasInternet()) {
                getNoNetworkDialog().show(); //notice user that application requires internet
            } else {
                startSelectingCityActivity();
            }
        } else {
            startForecastActivity(lastClientLocation);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noInternetDialog != null) {
            noInternetDialog.dismiss();
        }
    }

    private void startForecastActivity(ClientLocation lastClientLocation) {
        Intent intent = new Intent(this, ForecastActivity.class);
        intent.putExtra(WeatherApplication.CLIENT_LOCATION_ARG_NAME, lastClientLocation);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void startSelectingCityActivity() {
        Intent intent = new Intent(this, SelectingCityActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private AlertDialog getNoNetworkDialog() {
        if (noInternetDialog == null) noInternetDialog = createNoInternetDialog();
        return noInternetDialog;
    }

    private AlertDialog createNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_TRADITIONAL);
        return builder.setTitle(getString(R.string.no_internet_dialog_title))
                .setMessage(getString(R.string.no_internet_dialog_text))
                .setNeutralButton(getString(R.string.no_internet_dialog_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startSelectingCityActivity();
                        dialogInterface.dismiss();
                    }
                }).create();
    }
}

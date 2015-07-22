package com.d2112.weather.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.d2112.weather.R;
import com.d2112.weather.service.LocationService;
import com.d2112.weather.service.LocationServiceImpl;

public class SelectingCityActivity extends Activity {
    private LocationService locationService;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        locationService = new LocationServiceImpl();

        View okButton = findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView cityNameInput = (TextView) findViewById(R.id.cityNameInput);
                String cityName = (String) cityNameInput.getText().toString();
                Intent intent = new Intent(SelectingCityActivity.this, ForecastActivity.class);
                intent.putExtra("cityName", cityName);
                startActivity(intent);
            }
        });
    }
}

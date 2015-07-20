package com.d2112.weather.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.d2112.weather.R;
import com.d2112.weather.service.LocationService;
import com.d2112.weather.service.LocationServiceImpl;

import java.util.List;
import java.util.Map;

public class SelectingCityActivity extends Activity {
    private LocationService locationService;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        locationService = new LocationServiceImpl();

        Map<String, List<String>> citiesByCountryMap = locationService.getCitiesByCountryMap();
        TextExpandableListAdapter adapter = new TextExpandableListAdapter(this, citiesByCountryMap);
        ExpandableListView countryListView = (ExpandableListView) findViewById(R.id.COUNTRIES_LIST);

        countryListView.setAdapter(adapter);

        //set redirect on next activity on click on city item
        countryListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                TextView childTextView = (TextView) v.findViewById(R.id.childTextView);
                String cityName = (String) childTextView.getText();
                Intent intent = new Intent(SelectingCityActivity.this, ForecastActivity.class);
                intent.putExtra("cityName", cityName);
                startActivity(intent);
                return true;
            }
        });
    }
}

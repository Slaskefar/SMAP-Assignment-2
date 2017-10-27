package com.example.jeppe.weatherapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.jeppe.weatherapp.services.WeatherService;

public class CityListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);

        Intent weatherIntent = new Intent(this, WeatherService.class);
        startService(weatherIntent);
    }
}

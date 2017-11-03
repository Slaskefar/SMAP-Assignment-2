package com.example.jeppe.weatherapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.jeppe.weatherapp.models.CityWeather;
import com.example.jeppe.weatherapp.services.WeatherService;
import com.google.gson.Gson;

import java.util.ArrayList;

public class CityListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);

        Intent weatherIntent = new Intent(this, WeatherService.class);
        startService(weatherIntent);

        LocalBroadcastManager.getInstance(this).registerReceiver(WeatherReciever, new IntentFilter("weather-event"));
    }

    private BroadcastReceiver WeatherReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle args = intent.getBundleExtra("weather");
            ArrayList<CityWeather> cityWeather = (ArrayList<CityWeather>) args.getSerializable("weatherObj");
            Log.d("cityWeather:", new Gson().toJson(cityWeather));
        }
    };
}

package com.example.jeppe.weatherapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.jeppe.weatherapp.models.CityWeather;
import com.example.jeppe.weatherapp.services.WeatherService;

import java.util.ArrayList;

public class CityListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);

        Intent weatherIntent = new Intent(this, WeatherService.class);
        startService(weatherIntent);
    }

//    private BroadcastReceiver WeatherReciever = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            ArrayList<CityWeather> weather = (ArrayList<CityWeather>)intent.getExtras().getSerializable("weather");
//        }
//    };
}

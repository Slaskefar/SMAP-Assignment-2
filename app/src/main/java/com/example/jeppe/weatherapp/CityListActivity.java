package com.example.jeppe.weatherapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.example.jeppe.weatherapp.models.CityWeather;

import java.io.Serializable;
import java.util.ArrayList;
import com.example.jeppe.weatherapp.Globals;

import com.example.jeppe.weatherapp.models.CityWeather;
import com.example.jeppe.weatherapp.services.WeatherService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;

import javax.microedition.khronos.opengles.GL;

public class CityListActivity extends AppCompatActivity {

    Button btnRefresh;
    Button btnAdd;
    ListView lviWeatherList;
    EditText edtCityName;
    int CITY_LIST_ACTIVITY_INTENT_CODE = 101;
    WeatherService weatherService;
    WeatherAdapter weatherAdapter;
    ArrayList<CityWeather> weatherDataList;
    private Boolean bound = false;
    private BroadcastReceiver weatherReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        this.weatherReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getBundleExtra("weather");
                ArrayList<CityWeather> weather = weatherService.getAllCityWeather();
                //do something with weather
                weatherAdapter.weatherData = weather;
                weatherDataList = weather;
                weatherAdapter.notifyDataSetChanged();
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(weatherReceiver, new IntentFilter("weather-event"));

        edtCityName = findViewById(R.id.edtCityName);
        weatherAdapter = new WeatherAdapter(this, weatherDataList);
        lviWeatherList = findViewById(R.id.lviWeatherList);
        lviWeatherList.setAdapter(weatherAdapter);
        lviWeatherList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewCityDetails(weatherDataList.get(i));
            }
        });


        btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });

        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCityToList();
            }
        });

        Intent intent = new Intent(this, WeatherService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void refresh() {
        weatherService.updateCityWeather();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            WeatherService.WeatherBinder binder = (WeatherService.WeatherBinder)iBinder;
            weatherService = binder.getService();
            bound = true;

            weatherDataList = weatherService.getAllCityWeather();
            weatherAdapter.weatherData = weatherDataList;
            weatherAdapter.notifyDataSetChanged();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(weatherReceiver);
        unbindService(mConnection);
        bound = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void addCityToList() {
        String city = edtCityName.getText().toString();
        weatherService.addWeatherCity(city);
    }

    private void viewCityDetails(CityWeather city) {
        Intent detailsIntent = new Intent(this, CityDetailsActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(Globals.CITY_DETAILS_SINGLE_CITY, city);
        detailsIntent.putExtra(Globals.CITY_DETAILS_BUNDLE, args);
        //probably should be start activity for result
        startActivity(detailsIntent);
    }
}

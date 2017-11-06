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
import com.example.jeppe.weatherapp.DAL.DataHelper;
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
    private DataHelper dataHelper;
    EditText edtCityName;
    int CITY_LIST_ACTIVITY_INTENT_CODE = 101;
    WeatherService weatherService;
    WeatherAdapter weatherAdapter;
    ArrayList<CityWeather> weatherDataList;
    private Boolean bound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        dataHelper = new DataHelper(this);
        //dataHelper.addCity(new CityWeather("testId","TestCity",10,12,"Some Description"));

        weatherDataList = dataHelper.getCities();
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


    }

    private void refresh() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, WeatherService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        bound = false;
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            WeatherService.WeatherBinder binder = (WeatherService.WeatherBinder)iBinder;
            weatherService = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(weatherReceiver, new IntentFilter("weather-event"));
        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(weatherReceiver);
        super.onPause();
    }

    private BroadcastReceiver weatherReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getBundleExtra("weather");
            ArrayList<CityWeather> weather = (ArrayList<CityWeather>)bundle.getSerializable("weatherObj");
            //do something with weather
            weatherAdapter.weatherData = weather;
            weatherAdapter.notifyDataSetChanged();
        }
    };

    private void addCityToList() {
        Intent serviceIntent = new Intent(this, WeatherService.class);
        serviceIntent.addCategory(Globals.WEATHER_SERVICE_ADD_CITY_WEATHER);
        String city = edtCityName.getText().toString();
        serviceIntent.putExtra(Globals.WEATHER_SERVICE_ADD_CITY_WEATHER_DATA, city);
        startService(serviceIntent);
    }

    private void viewCityDetails(CityWeather city) {
        Intent detailsIntent = new Intent(this, CityDetailsActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(Globals.CITY_DETAILS_SINGLE_CITY, city);
        detailsIntent.putExtra(Globals.CITY_DETAILS_BUNDLE, args);
        //probably should be start activity for result
        startActivityForResult(detailsIntent, CITY_LIST_ACTIVITY_INTENT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CITY_LIST_ACTIVITY_INTENT_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    //Do nothing
                    break;
                case Globals.REMOVE_CITY:
                    removeCity(data.getIntExtra(Globals.CITY_DETAILS_REMOVE_CITY, -1));
                    break;
            }
        }
    }

    private void removeCity(int cityId) {
        //Remove city here and refresh ui here
        CityWeather cityToRemove = new CityWeather();
        cityToRemove.id = cityId;
        dataHelper.deleteCity(cityToRemove);
        weatherDataList = dataHelper.getCities();
        weatherAdapter.weatherData = weatherDataList;
        weatherAdapter.notifyDataSetChanged();
    }
}

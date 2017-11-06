package com.example.jeppe.weatherapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import javax.microedition.khronos.opengles.GL;

public class CityListActivity extends AppCompatActivity {

    Button btnRefresh;
    Button btnAdd;
    ListView lviWeatherList;
    private DataHelper dataHelper;
    EditText edtCityName;
    int CITY_LIST_ACTIVITY_INTENT_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        dataHelper = new DataHelper(this);
        //dataHelper.addCity(new CityWeather("testId","TestCity",10,12,"Some Description"));

        final ArrayList<CityWeather> weatherDataList;
        weatherDataList = dataHelper.getCities();
        /*// Test code with list view
        for(int i=0;i<10;i++) {
            weatherDataList.add(new CityWeather("" + i, "WeatherData " + i, i, i, "Description " + i));
        }
        // End of test code*/
        edtCityName = findViewById(R.id.edtCityName);
        final WeatherAdapter weatherAdapter = new WeatherAdapter(this, weatherDataList);
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

                    break;
            }

        }
    }

    private void removeCity() {
        //Remove city here and refresh ui here
    }
}

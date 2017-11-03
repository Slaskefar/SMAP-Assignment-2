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
import com.example.jeppe.weatherapp.models.CityWeatherData;
import java.util.ArrayList;

import com.example.jeppe.weatherapp.models.CityWeather;
import com.example.jeppe.weatherapp.services.WeatherService;
import com.google.gson.Gson;

import java.util.ArrayList;

public class CityListActivity extends AppCompatActivity {

    Button btnRefresh;
    Button btnAdd;
    ListView lviWeatherList;
    private DataHelper dataHelper;
    EditText edtCityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        dataHelper = new DataHelper(this);
        //dataHelper.addCity(new CityWeatherData("testId","TestCity",10,12,"Some Description"));

        final ArrayList<CityWeatherData> weatherDataList;
        weatherDataList = dataHelper.getCities();
        /*// Test code with list view
        for(int i=0;i<10;i++) {
            weatherDataList.add(new CityWeatherData("" + i, "WeatherData " + i, i, i, "Description " + i));
        }
        // End of test code*/
        edtCityName = findViewById(R.id.edtCityName);
        final WeatherAdapter weatherAdapter = new WeatherAdapter(this, weatherDataList);
        lviWeatherList = findViewById(R.id.lviWeatherList);
        lviWeatherList.setAdapter(weatherAdapter);
        lviWeatherList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewCityDetails(weatherDataList.get(i).cityName);
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
        // Call method on service
    }

    private void viewCityDetails(String city) {
        Intent detailsIntent = new Intent(this, CityDetailsActivity.class);
        //Put data extras here
        detailsIntent.putExtra("CITYNAME", city);
        //probably should be start activity for result
        startActivity(detailsIntent);
    }
}

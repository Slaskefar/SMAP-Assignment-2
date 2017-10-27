package com.example.jeppe.weatherapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.jeppe.weatherapp.models.CityWeatherData;

import java.util.ArrayList;

public class CityListActivity extends AppCompatActivity {

    Button btnRefresh;
    Button btnAdd;
    ListView lviWeatherList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);

        // Test code with list view
        ArrayList<CityWeatherData> weatherDataList = new ArrayList<>();
        for(int i=0;i<10;i++) {
            weatherDataList.add(new CityWeatherData("" + i, "WeatherData " + i, i, i, "Description " + i));
        }
        //End of test code

        WeatherAdapter weatherAdapter = new WeatherAdapter(this, weatherDataList);
        lviWeatherList = findViewById(R.id.lviWeatherList);
        lviWeatherList.setAdapter(weatherAdapter);
        lviWeatherList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewCityDetails();
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

    }

    private void viewCityDetails() {
        Intent detailsIntent = new Intent(this, CityDetailsActivity.class);
        //Put data extras here

        //probably should be start activity for result
        startActivity(detailsIntent);
    }
}

package com.example.jeppe.weatherapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.jeppe.weatherapp.DAL.DataHelper;
import com.example.jeppe.weatherapp.models.CityWeather;

public class CityDetailsActivity extends AppCompatActivity {
    Button btnRemove;
    Button btnOk;
    TextView txtHumidity;
    TextView txtTemperature;
    TextView txtCityName;
    TextView txtDescription;
    CityWeather currentCityWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_details);

        Intent fromListIntent = getIntent();
        Bundle bundle = fromListIntent.getBundleExtra(Globals.CITY_DETAILS_BUNDLE);
        currentCityWeather = (CityWeather) bundle.getSerializable(Globals.CITY_DETAILS_SINGLE_CITY);

        txtCityName = findViewById(R.id.txtCityName);
        txtCityName.setText(currentCityWeather.cityName);

        txtDescription = findViewById(R.id.txtDescription);
        txtDescription.setText(currentCityWeather.weatherDescription);

        txtHumidity = findViewById(R.id.txtHumidity);
        txtHumidity.setText(Integer.toString(currentCityWeather.humidity));

        txtTemperature = findViewById(R.id.txtTemperature);
        txtTemperature.setText(Double.toString(currentCityWeather.temperature));

        btnRemove = findViewById(R.id.btnRemove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeCity();
            }
        });

        btnOk = findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ok();
            }
        });
    }

    private void removeCity() {
        Intent returnIntent = new Intent();
        setResult(Globals.REMOVE_CITY, returnIntent);
        finish();
    }

    private void ok() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
